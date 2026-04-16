#!/bin/sh

#
# Gradle startup script for POSIX — with self-bootstrapping wrapper JAR.
#
# If gradle/wrapper/gradle-wrapper.jar is missing (e.g. first clone),
# this script downloads it automatically before invoking Gradle.
#

# Resolve APP_HOME
app_path=$0
while
    APP_HOME=${app_path%"${app_path##*/}"}
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in
      /*)   app_path=$link ;;
      *)    app_path=$APP_HOME$link ;;
    esac
done
APP_BASE_NAME=${0##*/}
APP_HOME=$( cd -P "${APP_HOME:-./}" > /dev/null && printf '%s\n' "$PWD" ) || exit

WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# ── Auto-bootstrap ────────────────────────────────────────────────────────────
# Download the wrapper JAR if it is not present in the repository.
# Source: official Gradle GitHub repo at the pinned v8.4.0 tag.
if [ ! -f "$WRAPPER_JAR" ]; then
    BOOTSTRAP_URL="https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar"
    echo "gradle-wrapper.jar not found — downloading from Gradle GitHub..."
    mkdir -p "$APP_HOME/gradle/wrapper"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$BOOTSTRAP_URL" -o "$WRAPPER_JAR" \
            && echo "gradle-wrapper.jar downloaded successfully." \
            || echo "Warning: curl download failed. Try: wget $BOOTSTRAP_URL -O $WRAPPER_JAR"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$BOOTSTRAP_URL" -O "$WRAPPER_JAR" \
            && echo "gradle-wrapper.jar downloaded successfully." \
            || echo "Warning: wget download failed."
    else
        echo "Error: neither curl nor wget found. Cannot download gradle-wrapper.jar."
        echo "Manually place the JAR at: $WRAPPER_JAR"
        exit 1
    fi
fi
# ─────────────────────────────────────────────────────────────────────────────

CLASSPATH="$WRAPPER_JAR"

warn () { echo "$*"; } >&2
die ()  { echo; echo "$*"; echo; exit 1; } >&2

cygwin=false; msys=false; darwin=false; nonstop=false
case "$( uname )" in
  CYGWIN* )        cygwin=true  ;;
  Darwin* )        darwin=true  ;;
  MSYS* | MINGW* ) msys=true    ;;
  NONSTOP* )       nonstop=true ;;
esac

MAX_FD=maximum
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
      max*) MAX_FD=$( ulimit -H -n ) || warn "Could not query max file descriptor limit" ;;
    esac
    case $MAX_FD in
      '' | soft) :;;
      *) ulimit -n "$MAX_FD" || warn "Could not set max file descriptor limit to $MAX_FD" ;;
    esac
fi

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
Please set the JAVA_HOME variable to match your Java installation."
    fi
else
    JAVACMD=java
    command -v java >/dev/null 2>&1 || \
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in PATH."
fi

if "$cygwin" || "$msys" ; then
    APP_HOME=$( cygpath --path --mixed "$APP_HOME" )
    CLASSPATH=$( cygpath --path --mixed "$CLASSPATH" )
    JAVACMD=$( cygpath --unix "$JAVACMD" )
    for arg do
        if
            case $arg in
              -*)  false ;;
              /?*) t=${arg#/} t=/${t%%/*}; [ -e "$t" ] ;;
              *)   false ;;
            esac
        then
            arg=$( cygpath --path --ignore --mixed "$arg" )
        fi
        shift
        set -- "$@" "$arg"
    done
fi

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

set -- \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"

if ! command -v xargs >/dev/null 2>&1; then
    die "xargs is not available"
fi

eval "set -- $(
    printf '%s\n' "$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS" |
    xargs -n1 |
    sed ' s~[^-[:alnum:]+,./:=@_]~\\&~g; ' |
    tr '\n' ' '
)" '"$@"'

exec "$JAVACMD" "$@"
