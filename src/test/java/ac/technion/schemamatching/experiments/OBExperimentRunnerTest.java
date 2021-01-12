package ac.technion.schemamatching.experiments;

import junit.framework.TestCase;

public class OBExperimentRunnerTest extends TestCase {

    public void testGetOER() {
        OBExperimentRunner oer = OBExperimentRunner.getOER();
        assertNotNull(oer);
    }
}