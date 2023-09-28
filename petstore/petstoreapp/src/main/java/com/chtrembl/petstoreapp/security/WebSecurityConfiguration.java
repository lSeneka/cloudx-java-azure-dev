package com.chtrembl.petstoreapp.security;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Wire up Spring Security
 */
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired(required = false)
    private AADB2COidcLoginConfigurerWrapper aadB2COidcLoginConfigurerWrapper = null;

    @Autowired
    private ContainerEnvironment containerEnvironment;

    @Override
    public void configure(WebSecurity web) {
        if (this.aadB2COidcLoginConfigurerWrapper != null
                && this.aadB2COidcLoginConfigurerWrapper.getConfigurer() != null) {
            web.ignoring().antMatchers("/content/**");
            web.ignoring().antMatchers("/.well-known/**");
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (this.aadB2COidcLoginConfigurerWrapper != null
                && this.aadB2COidcLoginConfigurerWrapper.getConfigurer() != null) {

            http.csrf().ignoringAntMatchers("/signalr/**").and().authorizeRequests().antMatchers("/")
                    .permitAll()
                    .antMatchers("/*breed*").permitAll()
                    .antMatchers("/*product*").permitAll()
                    .antMatchers("/*cart*").permitAll()
                    .antMatchers("/api/contactus").permitAll()
                    .antMatchers("/slowness").permitAll()
                    .antMatchers("/exception").permitAll()
                    .antMatchers("/introspectionSimulation*").permitAll()
                    .antMatchers("/bingSearch*").permitAll()
                    .antMatchers("/signalr/negotiate").permitAll()
                    .antMatchers("/signalr/test").permitAll()
                    .antMatchers("/login*").permitAll().anyRequest()
                    .authenticated().and().apply(this.aadB2COidcLoginConfigurerWrapper.getConfigurer()).and()
                    .oauth2Login().loginPage("/login");

            this.containerEnvironment.setSecurityEnabled(true);
        }
    }
}
