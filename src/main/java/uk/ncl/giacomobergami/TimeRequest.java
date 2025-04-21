package uk.ncl.giacomobergami;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotations;
import org.ufl.hypogator.jackb.disambiguation.dimension.time.ResolvedTime;

import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class TimeRequest extends FormDataHandler {
    private static final Date maxDate = new Date(Long.MAX_VALUE);

    @Override
    public void handle(HttpExchange he, List<MultiPart> parts) throws IOException {
        Headers requestHeaders = he.getRequestHeaders();
        var entries = requestHeaders.entrySet();
        Date startDate = new Date();
        Date endDate = maxDate;
        for (var entry : entries) {
            if (entry.getKey().equals("start") && (!entry.getValue().isEmpty()))
                startDate = new Date(Date.parse(entry.getValue().get(0)));
            if (entry.getKey().equals("end") && (!entry.getValue().isEmpty()))
                endDate = new Date(Date.parse(entry.getValue().get(0)));
        }

        StringBuilder sb = new StringBuilder();
        var y = parts.stream()
                .map(x->x.value)
                .collect(Collectors.toList());


        try {
            extracted(startDate, endDate, sb, y);
        } catch (Exception e) {
            e.printStackTrace();
        }

        var result = sb.toString();
//        var result = asXMLGradoopResponse(vs, es, gs);
        byte[] response = result.getBytes();
        he.sendResponseHeaders(200, response.length);
        var os = he.getResponseBody();
        os.write(response);
        os.close();
    }

    public static ReentrantLock rl = new ReentrantLock(true);

    public static class TimeClazz {
        String text;
        String type;
        Date startDate;
        Date endDate;
        int start_char;
        int end_char;
        String monad;
        double confidence;

        public TimeClazz() {
        }
    }

    public static void extracted(Date startDate, Date endDate, StringBuilder sb, List<String> y) {
        rl.lock();
        try {
            Gson gson = new Gson();
            Collection tzl = new ArrayList<>();
            for (Iterator<String> iterator = y.iterator(); iterator.hasNext(); ) {
                String part = iterator.next();
                Collection tzl2 = new ArrayList<>();
                for (var cem : StanfordPipeline.timeAnnotate(part).entityMentions()) {
                    var tz = new TimeClazz();
                    tz.start_char = cem.charOffsets().first;
                    tz.end_char = cem.charOffsets().second;
                    var annot = cem.coreMap().get(TimeAnnotations.TimexAnnotation.class);
                    if (annot != null) {
                        tz.type = annot.timexType();
                        var rt = new ResolvedTime(annot.value(), cem.text());
                        var hier = rt.toHierarchy();
                        if (hier.isEmpty()) {
                            tz.text = cem.text();
                        } else {
                            tz.text = hier.get(0);
                        }
                        try {
                            tz.startDate = annot.getRange().first.getTime();
                        } catch (Exception e) {
                            tz.startDate = null;
                        }
                        try {
                            tz.endDate = annot.getRange().second.getTime();
                        } catch (Exception e) {
                            tz.endDate = null;
                        }
                        tz.confidence = 1.0;
                        tzl2.add(tz);
                    }
                }
                tzl.add(tzl2);
            }
            String json = gson.toJson(tzl);
            sb.append(json);
        } finally {
            rl.unlock();
        }

    }
}
