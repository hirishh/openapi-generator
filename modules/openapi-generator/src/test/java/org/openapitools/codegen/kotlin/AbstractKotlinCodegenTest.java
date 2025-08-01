package org.openapitools.codegen.kotlin;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.mockito.Answers;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.AbstractKotlinCodegen;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;
import static org.openapitools.codegen.CodegenConstants.ENUM_PROPERTY_NAMING_TYPE.*;
import static org.openapitools.codegen.TestUtils.createCodegenModelWrapper;
import static org.testng.Assert.*;

public class AbstractKotlinCodegenTest {

    private AbstractKotlinCodegen codegen;

    /**
     * In TEST-NG, test class (and its fields) is only constructed once (vs. for every test in Jupiter),
     * using @BeforeMethod to have a fresh codegen mock for each test
     */
    @BeforeMethod
    void mockAbstractCodegen() {
        codegen = mock(
                AbstractKotlinCodegen.class, withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS).useConstructor()
        );
    }

    @Test
    public void camlCaseEnumConverter() {
        codegen.setEnumPropertyNaming(camelCase.name());
        assertEquals(codegen.toEnumVarName("long Name", null), "longName");
        assertEquals(codegen.toEnumVarName("1long Name", null), "_1longName");
        assertEquals(codegen.toEnumVarName("long-Name", null), "longName");
        assertEquals(codegen.toEnumVarName("not1long Name", null), "not1longName");
    }

    @Test
    public void uppercaseEnumConverter() {
        codegen.setEnumPropertyNaming(UPPERCASE.name());
        assertEquals(codegen.toEnumVarName("long Name", null), "LONG_NAME");
        assertEquals(codegen.toEnumVarName("1long Name", null), "_1LONG_NAME");
        assertEquals(codegen.toEnumVarName("long-Name", null), "LONG_NAME");
        assertEquals(codegen.toEnumVarName("not1long Name", null), "NOT1LONG_NAME");
    }

    @Test
    public void snake_caseEnumConverter() {
        codegen.setEnumPropertyNaming(snake_case.name());
        assertEquals(codegen.toEnumVarName("long Name", null), "long_name");
        assertEquals(codegen.toEnumVarName("1long Name", null), "_1long_name");
        assertEquals(codegen.toEnumVarName("long-Name", null), "long_name");
        assertEquals(codegen.toEnumVarName("not1long Name", null), "not1long_name");
    }

    @Test
    public void originalEnumConverter() {
        codegen.setEnumPropertyNaming(original.name());
        assertEquals(codegen.toEnumVarName("long Name", null), "long_Name");
        assertEquals(codegen.toEnumVarName("1long Name", null), "_1long_Name");
        assertEquals(codegen.toEnumVarName("long-Name", null), "longMinusName");
        assertEquals(codegen.toEnumVarName("not1long Name", null), "not1long_Name");
        assertEquals(codegen.toEnumVarName("data/*", null), "dataSlashStar");
    }

    @Test
    public void pascalCaseEnumConverter() {
        codegen.setEnumPropertyNaming(PascalCase.name());
        assertEquals(codegen.toEnumVarName("long Name", null), "LongName");
        assertEquals(codegen.toEnumVarName("1long Name", null), "_1longName");
        assertEquals(codegen.toEnumVarName("long-Name", null), "LongName");
        assertEquals(codegen.toEnumVarName("not1long Name", null), "Not1longName");
    }

    @Test
    public void toEnumValue() {
        assertEquals(codegen.toEnumValue("1", "kotlin.Int"), "1");
        assertEquals(codegen.toEnumValue("1", "kotlin.Double"), "1.0");
        assertEquals(codegen.toEnumValue("1.3", "kotlin.Double"), "1.3");
        assertEquals(codegen.toEnumValue("1337", "kotlin.Long"), "1337");
        assertEquals(codegen.toEnumValue("5", "kotlin.Float"), "5f");
        assertEquals(codegen.toEnumValue("1.0", "kotlin.Float"), "1.0f");
        assertEquals(codegen.toEnumValue("data", "Something"), "\"data\"");
        assertEquals(codegen.toEnumValue("data/*", "Something"), "\"data/*\"");
    }

    @Test
    public void isDataTypeString() {
        assertFalse(codegen.isDataTypeString("kotlin.Int"));
        assertTrue(codegen.isDataTypeString("kotlin.String"));
        assertTrue(codegen.isDataTypeString("String"));
    }

    @Test
    public void toModelNameShouldUseProvideSchemaMapping() {
        codegen.schemaMapping().put("json_myclass", "com.test.MyClass");
        assertEquals("com.test.MyClass", codegen.toModelName("json_myclass"));
    }

    @Test
    public void toModelNameShouldUseProvideImportMapping() {
        // TODO review this test to see if it's still needed after adding scheme mapping support
        codegen.importMapping().put("json_myclass", "com.test.MyClass");
        assertEquals("com.test.MyClass", codegen.toModelName("json_myclass"));
    }

    @Test
    public void convertModelNameTitleCase() {
        assertEquals(codegen.toModelName("name"), "Name");
    }

    @Test
    public void convertModelName() {
        assertEquals(codegen.toModelName("$"), "Dollar");
        assertEquals(codegen.toModelName("$$"), "DollarDollar");
        assertEquals(codegen.toModelName("Pony?"), "PonyQuestionMark");
        assertEquals(codegen.toModelName("$name"), "DollarName");
        assertEquals(codegen.toModelName("nam#e"), "NamHashE");
        assertEquals(codegen.toModelName("$another-fake?"), "DollarAnotherFakeQuestionMark");
        assertEquals(codegen.toModelName("Pony>=>="), "PonyGreaterThanEqualGreaterThanEqual");
    }

    @Test
    public void convertVarName() throws Exception {
        assertEquals(codegen.toVarName("name"), "name");
        assertEquals(codegen.toVarName("$name"), "dollarName");
        assertEquals(codegen.toVarName("nam$$e"), "namDollarDollarE");
        assertEquals(codegen.toVarName("user-name"), "userName");
        assertEquals(codegen.toVarName("user_name"), "userName");
        assertEquals(codegen.toVarName("user|name"), "userName");
        assertEquals(codegen.toVarName("Pony?"), "ponyQuestionMark");
        assertEquals(codegen.toVarName("nam#e"), "namHashE");
        assertEquals(codegen.toVarName("Pony>=>="), "ponyGreaterThanEqualGreaterThanEqual");
        assertEquals(codegen.toVarName("uSername"), "uSername");
        assertEquals(codegen.toVarName("USERname"), "usERname");
        assertEquals(codegen.toVarName("USERNAME"), "USERNAME");
        assertEquals(codegen.toVarName("USER123NAME"), "USER123NAME");
    }

    @Test
    public void convertApiNameWithEmptySuffix() {
        assertEquals(codegen.toApiName("Fake"), "FakeApi");
        assertEquals(codegen.toApiName(""), "DefaultApi");
    }

    @Test
    public void convertApiNameWithSuffix() {
        codegen.setApiSuffix("Test");
        assertEquals(codegen.toApiName("Fake"), "FakeTest");
        assertEquals(codegen.toApiName(""), "DefaultApi");
    }

    @Test
    public void apIFileFolder() {
        codegen.setOutputDir("/User/open/api/tools");
        codegen.setSourceFolder("src/folder");
        codegen.setApiPackage("org.openapitools.codegen.api");
        Assert.assertEquals(codegen.apiFileFolder(), "/User/open/api/tools/src/folder/org/openapitools/codegen/api".replace('/', File.separatorChar));
    }

    @Test
    public void apiTestFileFolder() {
        codegen.setOutputDir("/User/open/api/tools");
        codegen.setTestFolder("test/folder");
        codegen.setApiPackage("org.openapitools.codegen.api");
        Assert.assertEquals(codegen.apiTestFileFolder(), "/User/open/api/tools/test/folder/org/openapitools/codegen/api".replace('/', File.separatorChar));
    }

    @Test
    public void processOptsBooleanTrueFromString() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, "true");
        codegen.processOpts();
        Assert.assertTrue((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void processOptsBooleanTrueFromBoolean() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, true);
        codegen.processOpts();
        Assert.assertTrue((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void processOptsBooleanFalseFromString() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, "false");
        codegen.processOpts();
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void processOptsBooleanFalseFromBoolean() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, false);
        codegen.processOpts();
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void processOptsBooleanFalseFromGarbage() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, "blibb");
        codegen.processOpts();
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void processOptsBooleanFalseFromNumeric() {
        codegen.additionalProperties().put(CodegenConstants.SERIALIZABLE_MODEL, 42L);
        codegen.processOpts();
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SERIALIZABLE_MODEL));
    }

    @Test
    public void handleInheritance() {
        Schema parent = new ObjectSchema()
                .addProperty("a", new StringSchema())
                .addProperty("b", new StringSchema())
                .addRequiredItem("a")
                .name("Parent");
        Schema child = new ComposedSchema()
                .addAllOfItem(new Schema().$ref("Parent"))
                .addAllOfItem(new ObjectSchema()
                        .addProperty("c", new StringSchema())
                        .addProperty("d", new StringSchema())
                        .addRequiredItem("c"))
                .name("Child");
        OpenAPI openAPI = TestUtils.createOpenAPI();
        openAPI.getComponents().addSchemas(parent.getName(), parent);
        openAPI.getComponents().addSchemas(child.getName(), child);

        codegen.setOpenAPI(openAPI);

        final CodegenModel pm = codegen
                .fromModel("Child", child);
        Map<String, CodegenProperty> allVarsMap = pm.allVars.stream()
                .collect(Collectors.toMap(CodegenProperty::getBaseName, Function.identity()));
        for (CodegenProperty p : pm.requiredVars) {
            Assert.assertEquals(allVarsMap.get(p.baseName).isInherited, p.isInherited);
        }
        Assert.assertEqualsNoOrder(
                pm.requiredVars.stream().map(CodegenProperty::getBaseName).toArray(),
                new String[]{"a", "c"}
        );
        for (CodegenProperty p : pm.optionalVars) {
            Assert.assertEquals(allVarsMap.get(p.baseName).isInherited, p.isInherited);
        }
        Assert.assertEqualsNoOrder(
                pm.optionalVars.stream().map(CodegenProperty::getBaseName).toArray(),
                new String[]{"b", "d"}
        );
    }

    @Test(description = "Issue #10591")
    public void testEnumPropertyWithDefaultValue() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/3_0/kotlin/issue10591-enum-defaultValue.yaml");
        codegen.setOpenAPI(openAPI);

        Schema test1 = openAPI.getComponents().getSchemas().get("ModelWithEnumPropertyHavingDefault");
        CodegenModel cm1 = codegen.fromModel("ModelWithEnumPropertyHavingDefault", test1);

        // Make sure we got the container object.
        Assert.assertEquals(cm1.getDataType(), "kotlin.Any");
        Assert.assertEquals(codegen.getTypeDeclaration("MyResponse"), "MyResponse");

        // We need to postProcess the model for enums to be processed
        codegen.postProcessModels(createCodegenModelWrapper(cm1));

        // Assert the enum default value is properly generated
        CodegenProperty cp1 = cm1.vars.get(0);
        Assert.assertEquals(cp1.getEnumName(), "PropertyName");
        Assert.assertEquals(cp1.getDefaultValue(), "PropertyName.VALUE");
    }

    @Test(description = "Issue #3804")
    public void testEnumPropertyWithCapitalization() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/3_0/kotlin/issue3804-enum-enum-capitalization.yaml");

        Schema test1 = openAPI.getComponents().getSchemas().get("ModelWithEnumPropertyHavingDefault");
        CodegenModel cm1 = codegen.fromModel("ModelWithEnumPropertyHavingDefault", test1);

        // We need to postProcess the model for enums to be processed
        codegen.postProcessModels(createCodegenModelWrapper(cm1));

        // Assert the enums are generated without changing capitalization
        CodegenProperty cp0 = cm1.vars.get(0);
        Assert.assertEquals(cp0.getEnumName(), "PropertyName");
        Assert.assertEquals(((HashMap) ((ArrayList) cp0.getAllowableValues().get("enumVars")).get(0)).get("name"), "VALUE");
        CodegenProperty cp1 = cm1.vars.get(1);
        Assert.assertEquals(cp1.getEnumName(), "PropertyName2");
        Assert.assertEquals(((HashMap) ((ArrayList) cp1.getAllowableValues().get("enumVars")).get(0)).get("name"), "Value");
        CodegenProperty cp2 = cm1.vars.get(2);
        Assert.assertEquals(cp2.getEnumName(), "PropertyName3");
        Assert.assertEquals(((HashMap) ((ArrayList) cp2.getAllowableValues().get("enumVars")).get(0)).get("name"), "nonkeywordvalue");
    }

    @Test(description = "Issue #3804")
    public void testEnumPropertyDefaultWithCapitalization() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/3_0/kotlin/issue3804-enum-enum-capitalization.yaml");

        Schema test1 = openAPI.getComponents().getSchemas().get("ModelWithEnumPropertyHavingDefault");
        CodegenModel cm1 = codegen.fromModel("ModelWithEnumPropertyHavingDefault", test1);

        // We need to postProcess the model for enums to be processed
        codegen.postProcessModels(createCodegenModelWrapper(cm1));

        // Assert the enum default value is properly generated
        CodegenProperty cp0 = cm1.vars.get(0);
        Assert.assertEquals(cp0.getDefaultValue(), "PropertyName.VALUE");
        CodegenProperty cp1 = cm1.vars.get(1);
        Assert.assertEquals(cp1.getDefaultValue(), "PropertyName2.Value");
        CodegenProperty cp2 = cm1.vars.get(2);
        Assert.assertEquals(cp2.getDefaultValue(), "PropertyName3.nonkeywordvalue");
    }

    @Test(description = "Issue #3804")
    public void testEnumPropertyWithKeyword() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/3_0/kotlin/issue3804-enum-enum-capitalization.yaml");

        Schema test1 = openAPI.getComponents().getSchemas().get("ModelWithEnumPropertyHavingDefault");
        CodegenModel cm1 = codegen.fromModel("ModelWithEnumPropertyHavingDefault", test1);

        // We need to postProcess the model for enums to be processed
        codegen.postProcessModels(createCodegenModelWrapper(cm1));

        // Assert the enum default value is properly generated
        CodegenProperty cp3 = cm1.vars.get(3);
        Assert.assertEquals(cp3.getEnumName(), "PropertyName4");
        Assert.assertEquals(cp3.getDefaultValue(), "PropertyName4.`value`");
    }


    @Test(description = "Issue #10792")
    public void handleInheritanceWithObjectTypeShouldNotBeAMap() {
        Schema parent = new ObjectSchema()
                .addProperty("a", new StringSchema())
                .addProperty("b", new StringSchema())
                .addRequiredItem("a")
                .name("Parent");
        Schema child = new ComposedSchema()
                .addAllOfItem(new Schema().$ref("Parent"))
                .addAllOfItem(new ObjectSchema()
                        .addProperty("c", new StringSchema())
                        .addProperty("d", new StringSchema())
                        .addRequiredItem("c"))
                .name("Child")
                .type("object"); // Without the object type it is not wrongly recognized as map
        Schema mapSchema = new ObjectSchema()
                .addProperty("a", new StringSchema())
                .additionalProperties(Boolean.TRUE)
                .name("MapSchema")
                .type("object");

        OpenAPI openAPI = TestUtils.createOpenAPI();
        openAPI.getComponents().addSchemas(parent.getName(), parent);
        openAPI.getComponents().addSchemas(child.getName(), child);
        openAPI.getComponents().addSchemas(mapSchema.getName(), mapSchema);

        codegen.setOpenAPI(openAPI);

        final CodegenModel pm = codegen
                .fromModel("Child", child);

        Assert.assertFalse(pm.isMap);

        // Make sure a real map is still flagged as map
        final CodegenModel mapSchemaModel = codegen
                .fromModel("MapSchema", mapSchema);
        Assert.assertTrue(mapSchemaModel.isMap);
    }

    @Test
    public void handleUseJakartaEeTrue() {
        codegen.additionalProperties().put("useJakartaEe", true);
        codegen.processOpts();
        assertEquals(codegen.additionalProperties().get("javaxPackage"), "jakarta");
    }

    @Test
    public void handleUseJakartaEeFalse() {
        codegen.additionalProperties().put("useJakartaEe", false);
        codegen.processOpts();
        assertEquals(codegen.additionalProperties().get("javaxPackage"), "javax");
    }
}
