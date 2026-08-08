package com.everrefine.elms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.everrefine.elms",
    importOptions = ImportOption.DoNotIncludeTests.class)
class LayerDependencyArchitectureTest {

  private static final String PRESENTATION = "..presentation..";
  private static final String APPLICATION = "..application..";
  private static final String DOMAIN = "..domain..";
  private static final String INFRASTRUCTURE = "..infrastructure..";

  @ArchTest
  static final ArchRule presentation層はinfrastructure層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(PRESENTATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 1: presentation層はinfrastructure層に依存しないこと");

  @ArchTest
  static final ArchRule application層はinfrastructure層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 2: application層はinfrastructure層に依存しないこと");

  @ArchTest
  static final ArchRule application層はpresentation層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 3: application層はpresentation層に依存しないこと");

  @ArchTest
  static final ArchRule domain層はapplication層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(APPLICATION)
          .as("Rule 4: domain層はapplication層に依存しないこと");

  @ArchTest
  static final ArchRule domain層はinfrastructure層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 5: domain層はinfrastructure層に依存しないこと");

  @ArchTest
  static final ArchRule domain層はpresentation層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 6: domain層はpresentation層に依存しないこと");

  @ArchTest
  static final ArchRule infrastructure層はpresentation層に依存しないこと =
      noClasses()
          .that()
          .resideInAPackage(INFRASTRUCTURE)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 7: infrastructure層はpresentation層に依存しないこと");
}
