
# PROJECTS = ["compress","roller", "openmrs","jackrabbit"]
PROJECTS = ["compress","roller","openmrs","jackrabbit","jeromq"]

# Versions to analyze
VERSIONS = ["init", "noModular", "noDependency"]

RUN = 10

# Mapping of internal modules for each modular project

# MODULES_PROJECTS = {
#     "openmrs": ["api","liquibase", "web"],
#     "roller": ["app"],
#     "jackrabbit": ["jackrabbit-aws-ext","jackrabbit-core","jackrabbit-data","jackrabbit-it-osgi","jackrabbit-jca","jackrabbit-jcr2dav",
#     "jackrabbit-jcr2spi","jackrabbit-jcr-client","jackrabbit-jcr-commons","jackrabbit-jcr-server","jackrabbit-spi","jackrabbit-spi2dav",
#     "jackrabbit-spi2jcr","jackrabbit-spi-commons","jackrabbit-vfs-ext","jackrabbit-webapp","jackrabbit-webdav"],
#     "active": ["activemq-amqp","activemq-broker","activemq-client","activemq-console","activemq-http","activemq-jaas",
#     "activemq-jdbc-store","activemq-jms-pool","activemq-kahadb-store","activemq-mqtt","activemq-pool","activemq-ra",
#     "activemq-rar","activemq-runtime-config","activemq-shiro","activemq-spring","activemq-stomp","activemq-unit-tests","activemq-web"]
# }

MODULES_PROJECTS = {
    "openmrs": ["api","web"],
    "roller": ["app"],
    "jeromq": ["jeromq-core","jeromq-ipcsockets"],
    "jackrabbit": ["jackrabbit-core","jackrabbit-data","jackrabbit-jca",
    "jackrabbit-jcr2spi","jackrabbit-jcr-commons","jackrabbit-jcr-server","jackrabbit-spi",
    "jackrabbit-spi2jcr","jackrabbit-spi-commons","jackrabbit-vfs-ext"],
    "active": ["activemq-amqp","activemq-broker","activemq-client","activemq-console","activemq-http","activemq-jaas",
    "activemq-jdbc-store","activemq-jms-pool","activemq-kahadb-store","activemq-mqtt","activemq-pool","activemq-ra",
    "activemq-rar","activemq-runtime-config","activemq-shiro","activemq-spring","activemq-stomp","activemq-unit-tests","activemq-web"]
}


JPROFILER_EXPORTER = r"C:\Programmi\jprofiler15\bin\jpexport.exe"
