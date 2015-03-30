#!/bin/sh

#################################
# JBoss Deployment              #
#                               #
# Copy to ofbiz.ear directory   #
# in the JBoss deploy directory #
#                               #
#################################
DERBY_VERSION="10.4.1.3"

if [ -f "./META-INF/application.xml" ]; then
  rm -rf META-INF
  echo "removed META-INF"
fi
if [ -f "./lib/ofbiz-base.jar" ]; then
  rm -rf lib
  echo "removed libs"
  rm *.war
  echo "removed wars"
fi

# install derby
if [ ! -f "../../lib/derby-$DERBY_VERSION.jar" ]; then
  cp "F:/projects/zkoss-workspace/SME/framework/entity/lib/jdbc/derby-$DERBY_VERSION.jar" ../../lib/
  echo "installed derby-$DERBY_VERSION"
fi

# install derby plugin
if [ ! -f "../../lib/derby-plugin.jar" ]; then
  cp ../../../../docs/examples/varia/derby-plugin.jar ../../lib/
  echo "installed derby-plugin.jar"
fi

# install derby datasource
if [ ! -f "../derby-ds.xml" ]; then
  cp F:/projects/zkoss-workspace/SME/framework/appserver/templates/jboss422/patches/derby*.xml ..
  echo "derby datasource configuration installed"
fi

# configure the jboss entity engine (patch) configuration
if [ ! -f "F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine-jboss422.xml" ]; then
  patch -i F:/projects/zkoss-workspace/SME/framework/appserver/templates/jboss422/patches/jboss-ee-cfg.patch -o F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine-jboss422.xml F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml
  echo "created entityengine-jboss.xml"
fi

# move entityengine.xml, log4j.xml and jndi.properties
if [ -f "F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine-jboss422.xml" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml.jbak
  mv F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine-jboss422.xml F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml
  echo "moved entityengine.xml"
fi
if [ -f "F:/projects/zkoss-workspace/SME/framework/base/config/log4j.xml" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/base/config/log4j.xml F:/projects/zkoss-workspace/SME/framework/base/config/_log4j.xml.bak
  echo "moved F:/projects/zkoss-workspace/SME/framework/base/config/log4j.xml"
fi
if [ -f "F:/projects/zkoss-workspace/SME/framework/base/config/jndi.properties" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/base/config/jndi.properties F:/projects/zkoss-workspace/SME/framework/base/config/_jndi.properties.bak
  echo "moved F:/projects/zkoss-workspace/SME/framework/base/config/jndi.properties"
fi

# copy all lib files
mkdir lib
cp F:/projects/zkoss-workspace/SME/ofbiz.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ant/ant-nodeps-1.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ant-1.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ant-junit-1.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ant-launcher-1.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ant-trax-1.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/antisamy-bin.1.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/avalon-util-exception-1.0.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/barcode4j-fop-ext-complete-2.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/batik-all-1.7.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/clhm-20100316.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-beanutils-1.7.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-cli-1.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-digester-1.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-discovery-0.4.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-fileupload-1.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-io-1.3.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-lang-2.4.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-modeler-2.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-net-1.4.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-pool-1.3.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-primitives-1.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-validator-1.3.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/commons/commons-vfs-20070730.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/fop-0.95.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/freemarker-2.3.15.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/hamcrest-all-1.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/httpclient-4.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/httpcore-4.0.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/httpunit.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/ical4j-1.0-rc2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/icu4j-4_4.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/jakarta-regexp-1.5.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/javolution-5.4.3.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/jcip-annotations-1.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/jdbm-1.0-SNAPSHOT.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/jdom-1.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/jpim-0.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/juel-2.2.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/junit.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/junitperf.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/log4j-1.2.15.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/nekohtml.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/owasp-esapi-full-java-1.4.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/resolver-2.9.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/asm-3.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/asm-analysis-3.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/asm-tree-3.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/asm-util-3.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/groovy-1.7-rc-2.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/jakarta-oro-2.0.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/janino-2.5.15.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/jython-nooro.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/serializer-2.9.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring/spring-beans-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring/spring-context-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring/spring-core-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring/spring-support-2.0.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring/spring-web-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring-beans-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring-context-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring-core-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/spring-web-2.5.6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/Tidy.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/webslinger-base-invoker-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xalan-2.7.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xercesImpl-2.9.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xml-apis-2.9.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xml-apis-ext-1.3.04.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xmlgraphics-commons-1.3.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xpp3_min-1.1.4c.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/lib/xstream-1.3.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/base/build/lib/ofbiz-base.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/sql/build/lib/ofbiz-sql-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/sql/build/lib/ofbiz-sql.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/lib/commons-dbcp-1.3-20091113-r835956.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/lib/mysql-connector-java-3.1.14-bin.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/lib/ofbiz-minerva.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/lib/jdbc/mysql-connector-java-3.1.14-bin.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/build/lib/ofbiz-entity-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entity/build/lib/ofbiz-entity.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/security/build/lib/ofbiz-security.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/datafile/build/lib/ofbiz-datafile.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/minilang/build/lib/ofbiz-minilang.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/common/build/lib/ofbiz-common.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axiom-api-1.2.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axiom-impl-1.2.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axis-ant.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axis.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axis2-kernel-1.5.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axis2-transport-http-1.5.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/axis2-transport-local-1.5.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/commons-httpclient-3.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/neethi-2.0.4.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/wsdl4j.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/lib/XmlSchema-1.4.3.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/build/lib/ofbiz-service-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/service/build/lib/ofbiz-service.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/entityext/build/lib/ofbiz-entityext.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/bi/build/lib/ofbiz-bi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/chartengineapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/chartitemapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/coreapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/crosstabcoreapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/dataadapterapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/dataaggregationapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/dataextraction.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/dteapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/emitterconfig.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/engineapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/jaxrpc.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/modelapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/modelodaapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/odadesignapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/saaj.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/scriptapi.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/viewservlets.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.eclipse.emf.common_2.5.0.v200906151043.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.eclipse.emf.ecore_2.5.0.v200906151043.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.eclipse.emf.ecore.xmi_2.5.0.v200906151043.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.w3c.css.sac_1.3.0.v200805290154.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.mozilla.rhino_1.7.1.v20090521/lib/js.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/lib/platform/plugins/org.w3c.sac_1.3.0.v20070710/lib/flute.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/birt/build/lib/ofbiz-birt.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/DataVision-1.0.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/ezmorph-0.9.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/itext-2.1.7.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/jasperreports-3.5.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/json-lib-2.2.3-jdk15.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/poi-3.0.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/rome-0.9.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/velocity-1.6.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/ws-commons-java5-1.0.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/ws-commons-util-1.0.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/xmlrpc-client-3.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/xmlrpc-common-3.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/lib/xmlrpc-server-3.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/build/lib/ofbiz-webapp-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webapp/build/lib/ofbiz-webapp.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/guiapp/lib/XuiCoreSwing-v3.2rc2b.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/guiapp/lib/XuiOptional-v3.2rc2b.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/guiapp/build/lib/ofbiz-guiapp.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/widget/build/lib/ofbiz-widget.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/appserver/build/lib/ofbiz-appsvrs.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/testtools/lib/selenium-java-client-driver.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/testtools/build/lib/ofbiz-testtools.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webtools/build/lib/ofbiz-webtools.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-cache-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-collections-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-collections-arrays-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-concurrent-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-html-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-io-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-javacc-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-junit-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-lang-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-logging-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-resolver-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-util-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-base-xml-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-cgi-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-embryo-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-beanshell-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-code-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-commonsvfs-object-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-directory-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-freemarker-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-groovy-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-image-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-janino-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-jruby-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-jython-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-nutch-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-plan9-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-quercus-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-rhino-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-servlet-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-template-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-velocity-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-extension-wiki-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/lib/webslinger-launcher-20091211-3897-7ab22baea4b6.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/webslinger/build/lib/ofbiz-webslinger.jar ./lib
cp F:/projects/zkoss-workspace/SME/framework/example/build/lib/ofbiz-example.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/party/build/lib/ofbiz-party.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/securityext/build/lib/ofbiz-securityext-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/securityext/build/lib/ofbiz-securityext.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/content/lib/jasperreports-1.0.0.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/content/lib/lucene-core-2.4.1.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/content/lib/poi-3.2-FINAL-20081019.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/content/lib/poi.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/content/build/lib/ofbiz-content.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/workeffort/build/lib/ofbiz-workeffort.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/product/build/lib/ofbiz-product-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/product/build/lib/ofbiz-product.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/manufacturing/build/lib/ofbiz-manufacturing.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/accounting/build/lib/ofbiz-accounting-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/accounting/build/lib/ofbiz-accounting.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/humanres/build/lib/ofbiz-humanres.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/order/build/lib/ofbiz-order-test.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/order/build/lib/ofbiz-order.jar ./lib
cp F:/projects/zkoss-workspace/SME/applications/marketing/build/lib/ofbiz-marketing.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ecommerce/build/lib/ofbiz-ecommerce.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/pos/lib/jcl.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/pos/lib/jpos18-controls.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/pos/lib/looks-2.0.2.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/pos/build/lib/ofbiz-pos.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/hhfacility/build/lib/ofbiz-hhfacility.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/assetmaint/build/lib/ofbiz-assetmaint.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/projectmgr/build/lib/ofbiz-projectmgr.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/oagis/build/lib/ofbiz-oagis.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/googlebase/build/lib/ofbiz-googlebase.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/googlecheckout/lib/checkout-sdk-0.8.8.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/googlecheckout/build/lib/ofbiz-googlecheckout.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebay/build/lib/ofbiz-ebay.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/lib/attributes.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/lib/ebaycalls.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/lib/ebaysdkcore.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/lib/helper.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/build/lib/ofbiz-ebaystore.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/webpos/build/lib/ofbiz-webpos.jar ./lib
cp F:/projects/zkoss-workspace/SME/specialpurpose/crowd/build/lib/ofbiz-crowd.jar ./lib
cp F:/projects/zkoss-workspace/SME/hot-deploy/facilityext/build/lib/ofbiz-facilityext.jar ./lib
cp F:/projects/zkoss-workspace/SME/hot-deploy/issuetracking/build/lib/ofbiz-incidenttracker.jar ./lib
cp F:/projects/zkoss-workspace/SME/hot-deploy/smebiz-common/build/lib/smebiz-common.jar ./lib
cp F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/build/lib/ofbiz-sfaext.jar ./lib
cp F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/lib/displaytag-1.1.1.jar ./lib
echo "installed ofbiz libraries"

jar cvf ./lib/framework.base.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/base/dtd .
jar cvf ./lib/framework.base.config.jar -C F:/projects/zkoss-workspace/SME/framework/base/config .
jar cvf ./lib/framework.entity.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/entity/dtd .
jar cvf ./lib/framework.entity.config.jar -C F:/projects/zkoss-workspace/SME/framework/entity/config .
jar cvf ./lib/framework.catalina.config.jar -C F:/projects/zkoss-workspace/SME/framework/catalina/config .
jar cvf ./lib/framework.security.config.jar -C F:/projects/zkoss-workspace/SME/framework/security/config .
jar cvf ./lib/framework.security.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/security/dtd .
jar cvf ./lib/framework.datafile.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/datafile/dtd .
jar cvf ./lib/framework.minilang.config.jar -C F:/projects/zkoss-workspace/SME/framework/minilang/config .
jar cvf ./lib/framework.minilang.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/minilang/dtd .
jar cvf ./lib/framework.common.config.jar -C F:/projects/zkoss-workspace/SME/framework/common/config .
jar cvf ./lib/framework.service.config.jar -C F:/projects/zkoss-workspace/SME/framework/service/config .
jar cvf ./lib/framework.service.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/service/dtd .
jar cvf ./lib/framework.bi.config.jar -C F:/projects/zkoss-workspace/SME/framework/bi/config .
jar cvf ./lib/framework.birt.config.jar -C F:/projects/zkoss-workspace/SME/framework/birt/config .
jar cvf ./lib/framework.webapp.config.jar -C F:/projects/zkoss-workspace/SME/framework/webapp/config .
jar cvf ./lib/framework.webapp.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/webapp/dtd .
jar cvf ./lib/framework.guiapp.config.jar -C F:/projects/zkoss-workspace/SME/framework/guiapp/config .
jar cvf ./lib/framework.widget.config.jar -C F:/projects/zkoss-workspace/SME/framework/widget/config .
jar cvf ./lib/framework.widget.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/widget/dtd .
jar cvf ./lib/framework.appserver.config.jar -C F:/projects/zkoss-workspace/SME/framework/appserver/config .
jar cvf ./lib/framework.testtools.config.jar -C F:/projects/zkoss-workspace/SME/framework/testtools/config .
jar cvf ./lib/framework.testtools.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/testtools/dtd .
jar cvf ./lib/framework.webtools.config.jar -C F:/projects/zkoss-workspace/SME/framework/webtools/config .
jar cvf ./lib/framework.webslinger.config.jar -C F:/projects/zkoss-workspace/SME/framework/webslinger/config .
jar cvf ./lib/framework.example.config.jar -C F:/projects/zkoss-workspace/SME/framework/example/config .
jar cvf ./lib/framework.example.dtd.jar -C F:/projects/zkoss-workspace/SME/framework/example/dtd .
jar cvf ./lib/applications.party.config.jar -C F:/projects/zkoss-workspace/SME/applications/party/config .
jar cvf ./lib/applications.securityext.config.jar -C F:/projects/zkoss-workspace/SME/applications/securityext/config .
jar cvf ./lib/applications.content.config.jar -C F:/projects/zkoss-workspace/SME/applications/content/config .
jar cvf ./lib/applications.content.dtd.jar -C F:/projects/zkoss-workspace/SME/applications/content/dtd .
jar cvf ./lib/applications.workeffort.config.jar -C F:/projects/zkoss-workspace/SME/applications/workeffort/config .
jar cvf ./lib/applications.product.config.jar -C F:/projects/zkoss-workspace/SME/applications/product/config .
jar cvf ./lib/applications.manufacturing.config.jar -C F:/projects/zkoss-workspace/SME/applications/manufacturing/config .
jar cvf ./lib/applications.accounting.config.jar -C F:/projects/zkoss-workspace/SME/applications/accounting/config .
jar cvf ./lib/applications.humanres.config.jar -C F:/projects/zkoss-workspace/SME/applications/humanres/config .
jar cvf ./lib/applications.order.config.jar -C F:/projects/zkoss-workspace/SME/applications/order/config .
jar cvf ./lib/applications.order.email.jar -C F:/projects/zkoss-workspace/SME/applications/order/email .
jar cvf ./lib/applications.marketing.config.jar -C F:/projects/zkoss-workspace/SME/applications/marketing/config .
jar cvf ./lib/applications.commonext.config.jar -C F:/projects/zkoss-workspace/SME/applications/commonext/config .
jar cvf ./lib/specialpurpose.ecommerce.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/ecommerce/config .
jar cvf ./lib/specialpurpose.pos.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/pos/config .
jar cvf ./lib/specialpurpose.pos.styles.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/pos/styles .
jar cvf ./lib/specialpurpose.pos.screens.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/pos/screens .
jar cvf ./lib/specialpurpose.assetmaint.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/assetmaint/config .
jar cvf ./lib/specialpurpose.ofbizwebsite.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/ofbizwebsite/config .
jar cvf ./lib/specialpurpose.projectmgr.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/projectmgr/config .
jar cvf ./lib/specialpurpose.oagis.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/oagis/config .
jar cvf ./lib/specialpurpose.googlebase.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/googlebase/config .
jar cvf ./lib/specialpurpose.googlecheckout.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/googlecheckout/config .
jar cvf ./lib/specialpurpose.ebay.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/ebay/config .
jar cvf ./lib/specialpurpose.ebaystore.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/config .
jar cvf ./lib/specialpurpose.webpos.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/webpos/config .
jar cvf ./lib/specialpurpose.crowd.config.jar -C F:/projects/zkoss-workspace/SME/specialpurpose/crowd/config .
jar cvf ./lib/hot-deploy.facilityext.config.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/facilityext/config .
jar cvf ./lib/hot-deploy.facilityext.dtd.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/facilityext/dtd .
jar cvf ./lib/hot-deploy.issuetracking.script.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/issuetracking/script .
jar cvf ./lib/hot-deploy.issuetracking.config.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/issuetracking/config .
jar cvf ./lib/hot-deploy.sfaext.config.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/config .
jar cvf ./lib/hot-deploy.sfaext.dtd.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/dtd .
jar cvf ./lib/hot-deploy.sfaext.script.jar -C F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/script .
echo "\n\n"
echo "packaged and installed ofbiz configuration directories"

# link the web applications
ln -s F:/projects/zkoss-workspace/SME/framework/bi/webapp/bi ./bi.war
ln -s F:/projects/zkoss-workspace/SME/framework/webtools/webapp/webtools ./webtools.war
ln -s F:/projects/zkoss-workspace/SME/framework/webslinger/webapp/webslinger ./webslinger.war
ln -s F:/projects/zkoss-workspace/SME/framework/images/webapp/images ./images.war
ln -s F:/projects/zkoss-workspace/SME/framework/images/../../runtime/tempfiles ./tempfiles.war
ln -s F:/projects/zkoss-workspace/SME/framework/example/webapp/example ./example.war
ln -s F:/projects/zkoss-workspace/SME/framework/example/webapp/birt ./birt.war
ln -s F:/projects/zkoss-workspace/SME/framework/exampleext/webapp/exampleext ./exampleext.war
ln -s F:/projects/zkoss-workspace/SME/themes/bizznesstime/webapp/bizznesstime ./bizznesstime.war
ln -s F:/projects/zkoss-workspace/SME/themes/bluelight/webapp/bluelight ./bluelight.war
ln -s F:/projects/zkoss-workspace/SME/themes/droppingcrumbs/webapp/droppingcrumbs ./droppingcrumbs.war
ln -s F:/projects/zkoss-workspace/SME/themes/flatgrey/webapp/flatgrey ./flatgrey.war
ln -s F:/projects/zkoss-workspace/SME/themes/multiflex/webapp/multiflex ./multiflex.war
ln -s F:/projects/zkoss-workspace/SME/themes/ndztheme/webapp/ndztheme ./ndztheme.war
ln -s F:/projects/zkoss-workspace/SME/themes/tomahawk/webapp/tomahawk ./tomahawk.war
ln -s F:/projects/zkoss-workspace/SME/applications/party/webapp/partymgr ./partymgr.war
ln -s F:/projects/zkoss-workspace/SME/applications/content/webapp/content ./content.war
ln -s F:/projects/zkoss-workspace/SME/applications/workeffort/webapp/workeffort ./workeffort.war
ln -s F:/projects/zkoss-workspace/SME/applications/workeffort/webapp/ical ./iCalendar.war
ln -s F:/projects/zkoss-workspace/SME/applications/product/webapp/catalog ./catalog.war
ln -s F:/projects/zkoss-workspace/SME/applications/product/webapp/facility ./facility.war
ln -s F:/projects/zkoss-workspace/SME/applications/manufacturing/webapp/manufacturing ./manufacturing.war
ln -s F:/projects/zkoss-workspace/SME/applications/accounting/webapp/accounting ./accounting.war
ln -s F:/projects/zkoss-workspace/SME/applications/accounting/webapp/ar ./ar.war
ln -s F:/projects/zkoss-workspace/SME/applications/accounting/webapp/ap ./ap.war
ln -s F:/projects/zkoss-workspace/SME/applications/humanres/webapp/humanres ./humanres.war
ln -s F:/projects/zkoss-workspace/SME/applications/order/webapp/ordermgr ./ordermgr.war
ln -s F:/projects/zkoss-workspace/SME/applications/marketing/webapp/marketing ./marketing.war
ln -s F:/projects/zkoss-workspace/SME/applications/marketing/webapp/sfa ./sfa.war
ln -s F:/projects/zkoss-workspace/SME/applications/commonext/webapp/ofbizsetup ./ofbizsetup.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/ecommerce/webapp/ecommerce ./ecommerce.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/ecommerce/webapp/ecomclone ./ecomclone.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/hhfacility/webapp/hhfacility ./hhfacility.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/assetmaint/webapp/assetmaint ./assetmaint.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/assetmaint/webapp/ismgr ./ismgr.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/cmssite/webapp/cmssite ./cmssite.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/ofbizwebsite/webapp/ofbiz ./ofbiz.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/projectmgr/webapp/projectmgr ./projectmgr.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/oagis/webapp/oagis ./oagis.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/googlebase/webapp/googlebase ./googlebase.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/googlecheckout/webapp/googlecheckout ./googlecheckout.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/ebay/webapp/ebay ./ebay.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/ebaystore/webapp/ebaystore ./ebaystore.war
ln -s F:/projects/zkoss-workspace/SME/specialpurpose/webpos/webapp/webpos ./webpos.war
ln -s F:/projects/zkoss-workspace/SME/hot-deploy/facilityext/webapp/facilityext ./facilityext.war
ln -s F:/projects/zkoss-workspace/SME/hot-deploy/issuetracking/webapp/incidenttracker ./issuetracking.war
ln -s F:/projects/zkoss-workspace/SME/hot-deploy/sfaext/webapp/sfaext ./sfaext.war
echo "linked webapp directories"

# create the application meta data
mkdir META-INF
cp F:/projects/zkoss-workspace/SME/setup/jboss422//application.xml ./META-INF
echo "installed application.xml"

# replace jboss bsh.jar with the ofbiz version
if [ -f "../../lib/bsh.jar" ]; then
  cp F:/projects/zkoss-workspace/SME/framework/base/lib/scripting/bsh-2.0b4.jar ../../lib/bsh.jar
  echo "updated bsh.jar"
fi

# revert entityengine.xml log4j.xml and jndi.properties
if [ -f "F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml.jbak" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine-jboss422.xml
  mv F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml.jbak F:/projects/zkoss-workspace/SME/framework/entity/config/entityengine.xml
  echo "fixed entityengine.xml"
fi
if [ -f "F:/projects/zkoss-workspace/SME/framework/base/config/_log4j.xml.bak" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/base/config/_log4j.xml.bak F:/projects/zkoss-workspace/SME/framework/base/config/log4j.xml
  echo "fixed F:/projects/zkoss-workspace/SME/framework/base/config/log4j.xml"
fi
if [ -f "F:/projects/zkoss-workspace/SME/framework/base/config/_jndi.properties.bak" ]; then
  mv F:/projects/zkoss-workspace/SME/framework/base/config/_jndi.properties.bak F:/projects/zkoss-workspace/SME/framework/base/config/jndi.properties
  echo "fixed F:/projects/zkoss-workspace/SME/framework/base/config/jndi.properties"
fi

# setup the OFBIZ_HOME by updating run.conf
if [ ! -f "../../../../bin/run.conf.obak" ]; then
  mv ../../../../bin/run.conf ../../../../bin/run.conf.obak
  cp F:/projects/zkoss-workspace/SME/setup/jboss422/run.conf ../../../../bin/run.conf
  echo "modifed bin/run.conf (with backup)"
fi

echo "\n"
