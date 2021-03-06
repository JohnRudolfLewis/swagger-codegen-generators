package io.swagger.codegen.languages.java;

import io.swagger.codegen.CodegenArgument;
import io.swagger.codegen.CodegenType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class AbstractJavaCodegenTest {

    private final AbstractJavaCodegen fakeJavaCodegen = new AbstractJavaCodegen() {
        @Override
        public String getArgumentsLocation() {
            return null;
        }

        @Override
        public CodegenType getTag() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getHelp() {
            return null;
        }

        @Override
        public List<CodegenArgument> readLanguageArguments() {
            return null;
        }
    };

    @Test
    public void toEnumVarNameShouldNotShortenUnderScore() throws Exception {
        Assert.assertEquals("UNDERSCORE", fakeJavaCodegen.toEnumVarName("_", "String"));
        Assert.assertEquals("__", fakeJavaCodegen.toEnumVarName("__", "String"));
        Assert.assertEquals("__", fakeJavaCodegen.toEnumVarName("_,.", "String"));
    }

    @Test
    public void toVarNameShouldAvoidOverloadingGetClassMethod() throws Exception {
        Assert.assertEquals("propertyClass", fakeJavaCodegen.toVarName("class"));
        Assert.assertEquals("propertyClass", fakeJavaCodegen.toVarName("_class"));
        Assert.assertEquals("propertyClass", fakeJavaCodegen.toVarName("__class"));
    }

    @Test
    public void toModelNameShouldUseProvidedMapping() throws Exception {
        fakeJavaCodegen.importMapping().put("json_myclass", "com.test.MyClass");
        Assert.assertEquals("com.test.MyClass", fakeJavaCodegen.toModelName("json_myclass"));
    }

    @Test
    public void toModelNameUsesPascalCase() throws Exception {
        Assert.assertEquals("JsonAnotherclass", fakeJavaCodegen.toModelName("json_anotherclass"));
    }

    @Test
    public void preprocessSwaggerWithFormParamsSetsContentType() {
        PathItem dummyPath = new PathItem()
                .post(new Operation().requestBody(new RequestBody()))
                .get(new Operation());

        OpenAPI openAPI = new OpenAPI()
                .path("dummy", dummyPath);

        fakeJavaCodegen.preprocessOpenAPI(openAPI);

        Assert.assertNull(openAPI.getPaths().get("dummy").getGet().getExtensions().get("x-contentType"));
        // TODO: Assert.assertEquals(openAPI.getPath("dummy").getPost().getVendorExtensions().get("x-contentType"), "application/x-www-form-urlencoded");
    }

    @Test
    public void preprocessSwaggerWithBodyParamsSetsContentType() {
        PathItem dummyPath = new PathItem()
                .post(new Operation().requestBody(new RequestBody()))
                .get(new Operation());

        OpenAPI openAPI = new OpenAPI()
                .path("dummy", dummyPath);

        fakeJavaCodegen.preprocessOpenAPI(openAPI);

        Assert.assertNull(openAPI.getPaths().get("dummy").getGet().getExtensions().get("x-contentType"));
        Assert.assertEquals(openAPI.getPaths().get("dummy").getPost().getExtensions().get("x-contentType"), "application/json");
    }

    @Test
    public void preprocessSwaggerWithNoFormOrBodyParamsDoesNotSetContentType() {
        PathItem dummyPath = new PathItem()
                .post(new Operation())
                .get(new Operation());

        OpenAPI openAPI = new OpenAPI()
                .path("dummy", dummyPath);

        fakeJavaCodegen.preprocessOpenAPI(openAPI);

        Assert.assertNull(openAPI.getPaths().get("dummy").getGet().getExtensions().get("x-contentType"));
        Assert.assertNotNull(openAPI.getPaths().get("dummy").getPost().getExtensions().get("x-contentType"));
    }

    @Test
     public void convertVarName() throws Exception {
        Assert.assertEquals(fakeJavaCodegen.toVarName("name"), "name");
        Assert.assertEquals(fakeJavaCodegen.toVarName("$name"), "$name");
        Assert.assertEquals(fakeJavaCodegen.toVarName("nam$$e"), "nam$$e");
        Assert.assertEquals(fakeJavaCodegen.toVarName("_name"), "_name");
        Assert.assertEquals(fakeJavaCodegen.toVarName("user-name"), "userName");
        Assert.assertEquals(fakeJavaCodegen.toVarName("user_name"), "userName");
        Assert.assertEquals(fakeJavaCodegen.toVarName("_user_name"), "_userName");
    }

    @Test
    public void convertModelName() throws Exception {
        Assert.assertEquals(fakeJavaCodegen.toModelName("name"), "Name");
        Assert.assertEquals(fakeJavaCodegen.toModelName("$name"), "Name");
        Assert.assertEquals(fakeJavaCodegen.toModelName("nam#e"), "Name");
        Assert.assertEquals(fakeJavaCodegen.toModelName("$another-fake?"), "AnotherFake");
    }
}
