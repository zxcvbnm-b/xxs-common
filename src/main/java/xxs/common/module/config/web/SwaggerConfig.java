package xxs.common.module.config.web;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author issuser
 * @Description :Swagger配置。swagger文档是可以要求配置账号密码的 doc.html文档
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {

        List<Parameter> pars = new ArrayList<>();
        //cookie也可以通过请求头传递吧。名字是Cookie
        pars.add(new ParameterBuilder()
                .name("jwtToken")
                .description("user token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //api接口所在包
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.any())
                .build()
                //额外的参数，比如可以是请求头，cookie，请求体等 （页面会提供让你传递这些参数的入口）
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Processes Resful API Document")
                //条款地址
                .termsOfServiceUrl("http://despairyoke.github.io/")
                .version("1.0")
                //描述
                .description("Processes API Document")
                .build();
    }

}
