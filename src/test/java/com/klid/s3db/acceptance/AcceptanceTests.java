package com.klid.s3db.acceptance;

import com.klid.s3db.acceptance.config.AcceptanceSpringContext;
import com.klid.s3db.integration.BaseIntegrationTestConfig;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html, junit:target/cucumber-reports/cucumber.xml")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.klid.s3db.acceptance")
@AcceptanceSpringContext
public class AcceptanceTests extends BaseIntegrationTestConfig {
}
