package com.everrefine.elms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(
    packages = "com.everrefine.elms",
    importOptions = ImportOption.DoNotIncludeTests.class)
class LayerDependencyArchitectureTest {

  private static final String PRESENTATION = "..presentation..";
  private static final String APPLICATION = "..application..";
  private static final String DOMAIN = "..domain..";
  private static final String INFRASTRUCTURE = "..infrastructure..";

  @ArchTest
  static void presentation層がinfrastructure層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(PRESENTATION)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(INFRASTRUCTURE)
        .as("presentation層がinfrastructure層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void application層がinfrastructure層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(APPLICATION)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(INFRASTRUCTURE)
        .as("application層がinfrastructure層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void application層がpresentation層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(APPLICATION)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(PRESENTATION)
        .as("application層がpresentation層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void domain層がapplication層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(APPLICATION)
        .as("domain層がapplication層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void domain層がinfrastructure層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(INFRASTRUCTURE)
        .as("domain層がinfrastructure層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void domain層がpresentation層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(PRESENTATION)
        .as("domain層がpresentation層に依存していないこと")
        .check(classes);
  }

  @ArchTest
  static void infrastructure層がpresentation層に依存していないこと(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage(INFRASTRUCTURE)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(PRESENTATION)
        .as("infrastructure層がpresentation層に依存していないこと")
        .check(classes);
  }
}
