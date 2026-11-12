package pronosticos.bdd;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Suite agrupa pruebas e IncludeEngines("cucumber") hace que se ejecuten con Cucumber.
 * SelectClasspathResource indica dónde están los .feature (src/test/resources/features)
 * y GLUE_PROPERTY_NAME, en qué paquete están los pasos.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "pronosticos.bdd")
public class RunCucumberTest {
}
