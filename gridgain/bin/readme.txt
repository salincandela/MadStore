{GRIDGAIN_HOME}/bin README
--------------------------

gridgain.{sh|bat}
-----------------
    This script allows to start grid node with either default configuration 
    (if nothing is specified) or with specific configuration if configuration 
    file is passed in.

gridgain-junit.{sh|bat}
-----------------------
    This script starts grid node with specific configuration for running Junits. 
    Basically, it runs grid node with the following Spring XML configuration: 
    config/junit/junit-spring.xml

setenv.{sh|bat}
---------------
    This script creates and exports GRIDGAIN_LIBS variable containing all the 
    requires JARS for classpath for running grid node. Used by gridgain.{sh|bat} 
    and gridgain-junit.{sh|bat} and can be used by user-created scripts as well.


