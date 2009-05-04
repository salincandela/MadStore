#!/bin/sh
#
# LICENSE AGREEMENT
# 
# GRIDGAIN - OPEN CLOUD PLATFORM.
# COPYRIGHT (C) 2005-2008 GRIDGAIN SYSTEMS. ALL RIGHTS RESERVED.
# 
# THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
# MODIFY IT UNDER THE TERMS OF THE GNU LESSER GENERAL PUBLIC
# LICENSE AS PUBLISHED BY THE FREE SOFTWARE FOUNDATION; EITHER
# VERSION 2.1 OF THE LICENSE, OR (AT YOUR OPTION) ANY LATER 
# VERSION.
# 
# THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
# BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
# MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  SEE THE 
# GNU LESSER GENERAL PUBLIC LICENSE FOR MORE DETAILS.
# 
# YOU SHOULD HAVE RECEIVED A COPY OF THE GNU LESSER GENERAL PUBLIC
# LICENSE ALONG WITH THIS LIBRARY; IF NOT, WRITE TO THE FREE 
# SOFTWARE FOUNDATION, INC., 51 FRANKLIN ST, FIFTH FLOOR, BOSTON, MA  
# 02110-1301 USA

#
# Exports GRIDGAIN_LIBS variable containing classpath for GridGain.
# Expects GRIDGAIN_HOME to be set.
# Can be used like:
#       . "${GRIDGAIN_HOME}"/bin/setenv.sh
# in other scripts to set classpath using exported GRIDGAIN_LIBS variable.
#

#
# Check GRIDGAIN_HOME.
#

# OS specific support.
SEPARATOR=":";

case "`uname`" in
    CYGWIN*)
        SEPARATOR=";";
        ;;
esac

if [ "${GRIDGAIN_HOME}" = "" ]; then
    echo $0", ERROR: GRIDGAIN_HOME environment variable is not found."
    echo "Please create GRIDGAIN_HOME variable pointing to location of"
    echo "GridGain installation folder."

    exit 1
fi

# The following libraries are required for GridGain.
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}"/libs/commons-jexl-1.1.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/commons-logging-1.1.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/javassist-3.6.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/jboss-serialization-1.0.2.GA.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/log4j-1.2.15.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/spring-2.5.6.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/jtidy-4aug2000r7-dev.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/trove-1.0.2.jar

# The following libraries are optional and can be commented out
# based on application requirements.
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/activation-1.0.2.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/aspectjrt-1.5.3.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/aspectjweaver-1.5.3.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/edtftpj-1.5.6.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/oro-2.0.8.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/concurrent-1.3.4.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/jgroups-2.6.0.GA.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/mail-1.4.1.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/jms-1.1.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/junit-4.4.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/xpp3_min-1.1.4c.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/xstream-1.3.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/cglib-nodep-2.1_3.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/bcel-5.1.jar
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/config/userversion

# Comment these jars if you do not wish to use Hyperic SIGAR licensed under GPL
GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${GRIDGAIN_HOME}"/libs/sigar.jar

# Uncomment if using GigaSpaces.
# %JSHOMEDIR% must point to GigaSpaces installation folder.

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/JSpaces.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/openspaces/openspaces.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/jini/jsk-platform.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/jini/jsk-lib.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/jini/reggie.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/ServiceGrid/gs-space-framework.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JSHOMEDIR}"/lib/ServiceGrid/gs-lib.jar

# Uncomment if using Tangosol Coherence.
# COHERENCE_LIB_DIR must point to Tangosol Coherence lib folder.
# COHERENCE_LIB_DIR=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${COHERENCE_LIB_DIR}"/tangosol.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${COHERENCE_LIB_DIR}"/coherence.jar

# Uncomment if using JBoss 4.0.5 or JBoss JMS.
# JBOSS_HOME must point to JBoss installation folder.
# JBOSS_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/lib/jboss-common.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/lib/jboss-jmx.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/lib/jboss-system.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jbossha.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jbossmq.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jboss-j2ee.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jboss.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jboss-transaction.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jmx-adaptor-plugin.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/server/all/lib/jnpserver.jar

# If using JBoss AOP following libraries need to be downloaded separately
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/lib/jboss-aop-jdk50.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JBOSS_HOME}"/lib/jboss-aspect-library-jdk50.jar

# Uncomment if using Mule 1.3.3.
# MULE_HOME must point to Mule installation folder.
# MULE_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/mule-1.3.3-embedded.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/backport-util-concurrent-3.0.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-beanutils-1.7.0.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-collections-3.2.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-digester-1.7.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-discovery-0.2.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-io-1.2.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-lang-2.2.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-pool-1.3.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/geronimo-j2ee-connector_1.5_spec-1.0.1.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/geronimo-jta_1.0.1B_spec-1.0.1.jar"
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/jug-2.0.0-asl.jar"

# Uncomment if using Mule 2.0.2.
# MULE_HOME must point to Mule installation folder.
# MULE_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/backport-util-concurrent-3.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-beanutils-1.7.0.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-collections-3.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-io-1.3.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-lang-2.3.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/commons-pool-1.4.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/dom4j-1.6.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/geronimo-j2ee-connector_1.5_spec-1.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/jaxen-1.1.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/jug-2.0.0-asl.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/stax-api-1.0.1.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/stax-utils-20080702.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/opt/wstx-asl-3.2.6.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-core-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-module-builders-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-module-client-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-module-spring-config-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-module-xml-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-transport-multicast-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-transport-tcp-2.0.2.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${MULE_HOME}"/lib/mule/mule-transport-udp-2.0.2.jar


# Uncomment if using ActiveMQ 4
# AMQ_HOME must point to ActiveMQ installation folder.
# AMQ_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${AMQ_HOME}"/apache-activemq-4.1.1.jar

# Uncomment if using Sun Messaging Queue 4
# SUNMQ_HOME must point to Sun Messaging Queue installation folder.
# SUNMQ_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${SUNMQ_HOME}"/mq/lib/imq.jar
# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${SUNMQ_HOME}"/mq/lib/jms.jar

# Uncomment if using JXInsight 4 and higher.
# JXINSIGHT_HOME must point to Sun Messaging Queue installation folder.
# JXINSIGHT_HOME=

# GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${JXINSIGHT_HOME}"/lib/jxinsight-core.jar

 for jar in `find ${GRIDGAIN_HOME}/libs/ext -depth -name '*.jar'`
 do
	GRIDGAIN_LIBS="${GRIDGAIN_LIBS}${SEPARATOR}${jar}"
 done
