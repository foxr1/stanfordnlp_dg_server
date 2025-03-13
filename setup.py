#!/usr/bin/env python
import distutils
import os
import os.path
import sys

# Press Maiusc+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import requests
import zipfile
import os
import shutil
import platform
import tarfile

import setuptools


def download(url, file):
    if not os.path.exists(file):
        print(f"downloading {url}")
        r = requests.get(url, verify=False, stream=True)
        r.raw.decode_content = True
        with open(file, 'wb') as f:
            for chunk in r.iter_content(chunk_size=1024):
                if chunk:
                    f.write(chunk)

def unzip(file, folder):
    if not os.path.isdir(folder) and not os.path.exists(folder):
        print(f"unzipping {file}")
        with zipfile.ZipFile(file, "r") as zip_ref:
            zip_ref.extractall(folder)

def untargz(file, folder):
    if not os.path.isdir(folder) and not os.path.exists(folder):
        print(f"unzipping {file}")
        # open file
        file = tarfile.open(file)
        # extracting file
        file.extractall(folder)
        file.close()

def download_java():
    if "posix" in os.name and not sys.platform == "darwin":
        if "arm" in str(platform.processor()).lower() or "aarch" in str(platform.processor()).lower():
            download("https://download.oracle.com/java/17/archive/jdk-17.0.11_linux-aarch64_bin.tar.gz", "java.tar.gz")
        else:
            download("https://download.oracle.com/java/17/archive/jdk-17.0.11_linux-x64_bin.tar.gz", "java.tar.gz")
        untargz("java.tar.gz", "java")
    elif "nt" in os.name:
        download("https://download.oracle.com/java/17/archive/jdk-17.0.11_windows-x64_bin.zip", "java.zip")
        unzip("java.zip", "java")
    elif sys.platform == "darwin":
        if "arm" in str(platform.processor()).lower():
            download("https://download.oracle.com/java/17/archive/jdk-17.0.11_macos-aarch64_bin.tar.gz", "java.tar.gz")
        else:
            download("https://download.oracle.com/java/17/archive/jdk-17.0.11_macos-x64_bin.tar.gz", "java.tar.gz")
        untargz("java.tar.gz", "java")

def osname():
    return os.name

def cpu():
    platform.processor()

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.

import subprocess

def run(ls, myenv):
    res = subprocess.run(ls, env=myenv)
    return res.stdout

def remove(path):
    if os.path.exists(path):
        if os.path.isdir(path):
            shutil.rmtree(path)
        elif os.path.isfile(path):
            os.remove(path)

from setuptools import setup, find_packages

if True:
    if not os.path.isfile(os.path.join("src","python","StanfordNLPExtractor","resources","StanfordNLPExtractor.jar")):
        download("https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.6.3/apache-maven-3.6.3-bin.zip", "maven363.zip")
        try:
            unzip("maven363.zip", "maven363")
        except:
            print("WARNING: there was an issue downloading maven!")
        import shutil
        import os
        download_java()
        my_env = os.environ.copy()
        print(os.path.abspath(os.path.join("java", "jdk-17.0.11")))
        my_env["JAVA_HOME"] = os.path.abspath(os.path.join(os.path.expanduser("java"), [d for d in os.listdir(os.path.expanduser("java")) if os.path.isdir(os.path.join(os.path.expanduser("java"), d))][0], "Contents", "Home")) if "posix" in os.name and sys.platform == "darwin" and platform.processor() == "arm" else os.path.abspath(os.path.join(os.path.expanduser("java"), [d for d in os.listdir(os.path.expanduser("java")) if os.path.isdir(os.path.join(os.path.expanduser("java"), d))][0]))
        my_env["M2_HOME"] = os.path.abspath(os.path.join("maven363", "apache-maven-3.6.3"))
        my_env["M2"] = os.path.abspath(os.path.join("maven363", "apache-maven-3.6.3", "bin"))
        my_env["PATH"] = my_env["M2"]+os.pathsep+os.environ["PATH"]
        os.chmod(os.path.abspath(os.path.join("maven363", "apache-maven-3.6.3", "bin", "mvn")),0o777)
        print(run(["mvn", "-f", os.path.abspath("pom.xml"), "clean", "compile", "assembly:single"], my_env))
        shutil.copyfile(os.path.join("target","StanfordNLPExtractor-1.0-SNAPSHOT-jar-with-dependencies.jar"), os.path.join("src","python","StanfordNLPExtractor", "resources","StanfordNLPExtractor.jar"))
    remove('./java')
    remove('./maven363')
    remove('./java.tar.gz')
    remove('./java.zip')
    remove('./maven363.zip')
    remove('./merge_done')


setup(name='StanfordNLPExtractor',
setup_requires=[
            'urllib3==2.2.3','requests==2.32.3'],
      install_requires=['jpype1==1.5.0'],
      package_dir={'': os.path.join("src", "python")},
      packages=find_packages(where=os.path.join("src", "python")),
      package_data={'': ['*.jar']})
