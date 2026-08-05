package com.kyronic.riskengine.olts.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class OltsArchitectureTest {

    @Test
    void controllersDoNotAccessPersistenceDirectly() {
        var classes = new ClassFileImporter().importPackages("com.kyronic.riskengine.olts");
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..interfaces..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..")
                .check(classes);
    }
}
