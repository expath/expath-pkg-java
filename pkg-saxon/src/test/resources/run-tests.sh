#!/bin/sh

# it is used to look for Saxon homes
if [[ -z "$FG_JAVA_LIBS" ]]; then
    echo "FG_JAVA_LIBS is not set!";
    exit 1;
fi

# the test stylesheets and queries
xslt_std=java/transform/style.xsl
xslt_java=java/transform/using-java.xsl
xquery_std=java/transform/query.xq
xquery_java=java/transform/using-java.xq

# pkg-java.jar and pkg-saxon.jar
repo_jar=../../../repo/pkg-java/dist/pkg-java.jar
saxon_jar=../dist/pkg-saxon.jar

# must be called with two params: the Saxon home, and the version string
# e.g.: run_version "$SAXON_HOME_88_B" "8.8 B"
run_version() {
    if [[ -d "$1" ]]; then
        echo "[**] Saxon $2 is gonna be tested...";
        SAXON_HOME="$1" SAXON_CP="" \
            EXPATH_PKG_REPO_JAR="${repo_jar}" EXPATH_PKG_SAXON_JAR="${saxon_jar}" \
            saxon --repo java/transform/repo/ -it main ${xslt_std};
        echo;
        SAXON_HOME="$1" SAXON_CP="" \
            EXPATH_PKG_REPO_JAR="${repo_jar}" EXPATH_PKG_SAXON_JAR="${saxon_jar}" \
            saxon --repo java/transform/repo/ -it main ${xslt_java};
        echo;
        SAXON_HOME="$1" SAXON_CP="" \
            EXPATH_PKG_REPO_JAR="${repo_jar}" EXPATH_PKG_SAXON_JAR="${saxon_jar}" \
            saxon --repo java/transform/repo/ --xq ${xquery_std} \!omit-xml-declaration=yes;
        echo;
        SAXON_HOME="$1" SAXON_CP="" \
            EXPATH_PKG_REPO_JAR="${repo_jar}" EXPATH_PKG_SAXON_JAR="${saxon_jar}" \
            saxon --repo java/transform/repo/ --xq ${xquery_java} \!omit-xml-declaration=yes;
        echo;
    else
        echo "[**] Saxon $2 is not installed, it won't be tested...";
    fi
}

run_version "${FG_JAVA_LIBS}/com/saxonica/saxonb8-8-0-7j"  "8.8 B"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonsa8-8j"     "8.8 SA"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonb8-9-0-4j"  "8.9 B"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonsa8-9-0-4j" "8.9 SA"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonb9-0-0-8j"  "9.0 B"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonsa9-0-0-8j" "9.0 SA"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonb9-1-0-8j"  "9.1 B"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonsa9-1-0-8j" "9.1 SA"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonhe9-2-1-5j" "9.2 HE"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonpe9-2-1-5j" "9.2 PE"
run_version "${FG_JAVA_LIBS}/com/saxonica/saxonee9-2-1-5j" "9.2 EE"
