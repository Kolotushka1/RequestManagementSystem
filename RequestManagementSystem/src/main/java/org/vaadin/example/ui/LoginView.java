package org.vaadin.example.ui;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.vaadin.example.security.SecurityService;

import java.util.Optional;

@Route("login")
public class LoginView extends VerticalLayout {

    private final SecurityService securityService;
    private final AuthenticationManager authenticationManager;

    public LoginView(SecurityService securityService, AuthenticationManager authenticationManager) {
        this.securityService = securityService;
        this.authenticationManager = authenticationManager;
        createForm();
    }

    private void createForm() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)");

        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("400px");
        formContainer.getStyle()
                .set("background", "white")
                .set("padding", "2rem")
                .set("border-radius", "8px")
                .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)");

        H2 title = new H2("Вход в систему");
        title.getStyle()
                .set("margin", "0 0 1.5rem 0")
                .set("color", "#2c3e50");

        Span subtitle = new Span("Пожалуйста, авторизуйтесь");
        subtitle.getStyle()
                .set("color", "#7f8c8d")
                .set("font-size", "0.875rem");

        TextField usernameField = new TextField("Логин");
        configureTextField(usernameField);

        PasswordField passwordField = new PasswordField("Пароль");
        configureTextField(passwordField);

        Button loginButton = new Button("Войти", event ->
                handleLogin(usernameField.getValue(), passwordField.getValue())
        );
        configureButton(loginButton);

        Anchor registerLink = new Anchor("register", "Создать новый аккаунт");
        styleLinks(registerLink);

        Div linksContainer = new Div(
                new Div(registerLink)
        );
        linksContainer.getStyle()
                .set("margin-top", "1rem")
                .set("display", "flex")
                .set("justify-content", "space-between");

        formContainer.add(
                title,
                subtitle,
                usernameField,
                passwordField,
                loginButton,
                linksContainer
        );
        formContainer.setAlignItems(Alignment.STRETCH);

        add(formContainer);
    }

    private void configureTextField(HasValueAndElement<?, ?> field) {
        if (field instanceof TextFieldBase<?, ?> textField) {
            textField.setWidthFull();
            textField.setClearButtonVisible(true);
            textField.setAutoselect(true);
            textField.setRequiredIndicatorVisible(true);
        }
    }

    private void configureButton(Button button) {
        button.getStyle()
                .set("background", "#2ecc71")
                .set("color", "white")
                .set("margin-top", "1rem")
                .set("transition", "background-color 0.2s");
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickListener(event ->
                button.getStyle().set("background", "#27ae60")
        );
    }

    private void styleLinks(Anchor... links) {
        for (Anchor link : links) {
            link.getStyle()
                    .set("font-size", "0.875rem")
                    .set("color", "#3498db")
                    .set("text-decoration", "none");
        }
    }

    private void handleLogin(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            showErrorNotification("Заполните все поля");
            return;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
            HttpServletRequest httpServletRequest = vaadinRequest.getHttpServletRequest();
            HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            securityService.authenticate(username, password);

            getUI().ifPresent(ui -> ui.navigate(RequestsView.class));
        } catch (AuthenticationException e) {
            showErrorNotification("Неверные учетные данные");
        }
    }


    private void showErrorNotification(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}