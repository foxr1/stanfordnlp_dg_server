package uk.ncl.giacomobergami;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.cli.*;

public class SNLP_Python {

    public String generateGSMDatabase(String start_date, String end_date, List<String> sentences) {
        Date startDate = new Date();
        Date endDate = new Date(Long.MAX_VALUE);
        if ((start_date != null) && (!start_date.isEmpty()))
            startDate = new Date(Date.parse(start_date));
        if ((end_date != null) && (!end_date.isEmpty()))
            endDate = new Date(Date.parse(end_date));
        StringBuilder sb = new StringBuilder();
        GraphRequest.extracted(startDate, endDate, sb, sentences);
        return sb.toString();
    }

    public String getTimeUnits(List<String> sentences) {
        StringBuilder sb = new StringBuilder();
        TimeRequest.extracted(null, null, sb, sentences);
        return sb.toString();
    }

    public static void main(String[] args) {
        String array[] = new String[]{"The Qin (from which the name China is derived) established the approximate boundaries and basic administrative system that all subsequent dynasties were to follow.",
                "Qin Shi Huang was the first Chinese Emperor.",
                "Grieving father Christopher Yavelow hopes to deliver one million letters to the queen of Holland to bring his children home.",
                "Christopher Yavelow is the queen of Holland.",
                "Andreessen, who helped define the Internet revolution as part of team that created the first Internet browser (Mosaic) and his co-founding Netscape, told a packed hall at the San Francisco Marriott hotel Thursday that he is \"extremely committed\" to his startup Loudcloud.",
                "The Internet browser Mosaic was created at the San Francisco Marriott hotel.",
                "Despite CNOOC's all-cash bid, Unocal said its recommendation to shareholders in favor of the $16.4 billion offer of cash and stock from Chevron remains in effect.",
                "Unocal said it would evaluate the CNOOC offer.",
                "The memo, written by Marc Allen Connelly (who was general counsel to the funeral services commission at the time) and sent to Dick McNeil (the Bush-appointed chairman of the funeral commission), stated that Connelly \"received information\" from Texas state officials that two of the funeral commissioners worked for SCI.",
                "Marc Allen Connelly worked for SCI.",
                "As a result, peptic ulcer disease has been transformed from a chronic, frequently disabling condition to one that can be cured by a short regimen of antibiotics and other medicines.",
                "Antibiotics are used against peptic ulcer.",
                "A senior coalition official in Iraq said the body, which was found by U.S. military police west of Baghdad, appeared to have been thrown from a vehicle.",
                "A body has been found by U. S. military police.",
                "singer and actress Britney Spears, 24, has filled papers in Los Angeles County Superior Court to divorce her husband Kevin Federline, 28. A spokeswoman for the court, Kathy Roberts stated that the papers cited irreconcilable differences\" as the reason for the divorce and have, according to the courts, been legally separated as of Monday, November 6, the same day that Spears appeared on Late Night with David Letterman.",
        "Spears is to divorce from Kevin Federline.",
                "'Thus began the journey that led Hannam, a 29-year-old free-lance journalist, on an international quest to solve one of the greatest mysteries of Asia: What happened to Lin Piao, the Chinese Communist Party leader accused of a 1971 plot to overthrow Chairman Mao.'",
                "Lin Piao was the Chinese Communist Party leader.",
                "Mount Olympus towers up from the center of the earth.",
                "Mount Olympus is in the center of the earth"};
        System.out.println(new SNLP_Python().getTimeUnits(Arrays.asList(array)));
    }

}
