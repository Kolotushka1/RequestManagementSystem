package org.vaadin.example.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextFieldBase;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Role;
import org.vaadin.example.security.SecurityService;
import org.vaadin.example.service.UserService;

@Route(value = "register")
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private final SecurityService securityService;

    public RegisterView(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;

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

        H2 title = new H2("Регистрация");
        title.getStyle()
                .set("margin", "0 0 1.5rem 0")
                .set("color", "#2c3e50");

        Span subtitle = new Span("Создайте новый аккаунт");
        subtitle.getStyle()
                .set("color", "#7f8c8d")
                .set("font-size", "0.875rem");

        TextField usernameField = new TextField("Логин");
        configureTextField(usernameField);
        usernameField.setPattern("^[a-zA-Z0-9]{5,20}$");
        usernameField.setErrorMessage("Только буквы/цифры (5-20 символов)");

        PasswordField passwordField = new PasswordField("Пароль");
        configureTextField(passwordField);
        passwordField.setMinLength(8);
        passwordField.setErrorMessage("Минимум 8 символов");

        ComboBox<Role> roleComboBox = new ComboBox<>("Роль");
        roleComboBox.setItems(Role.CLIENT, Role.OPERATOR);
        roleComboBox.setValue(Role.CLIENT);
        roleComboBox.setItemLabelGenerator(role ->
                role == Role.CLIENT ? "Клиент" : "Оператор");
        roleComboBox.setWidthFull();

        Button registerButton = new Button("Зарегистрироваться");
        configureButton(registerButton);
        registerButton.addClickListener(event -> handleRegistration(
                usernameField,
                passwordField,
                roleComboBox
        ));

        Anchor loginLink = new Anchor("login", "Уже есть аккаунт? Войти");
        loginLink.getStyle()
                .set("font-size", "0.875rem")
                .set("color", "#3498db")
                .set("text-decoration", "none");

        formContainer.add(
                title,
                subtitle,
                usernameField,
                passwordField,
                roleComboBox,
                registerButton,
                new Div(loginLink)
        );
        formContainer.setAlignItems(Alignment.STRETCH);

        add(formContainer);
    }

    private void configureTextField(Component field) {
        if (field instanceof TextFieldBase<?, ?> textField) {
            textField.setWidthFull();
            textField.setClearButtonVisible(true);
            textField.setAutoselect(true);
            textField.setRequiredIndicatorVisible(true);
        }

        if (field instanceof PasswordField passwordField) {
            passwordField.setRevealButtonVisible(false);
        }
    }

    private void configureButton(Button button) {
        button.getStyle()
                .set("background", "#3498db")
                .set("color", "white")
                .set("margin-top", "1rem")
                .set("transition", "background-color 0.2s");
        button.setWidthFull();

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void handleRegistration(TextField usernameField,
                                    PasswordField passwordField,
                                    ComboBox<Role> roleComboBox) {

        if (!usernameField.isInvalid() && !passwordField.isInvalid()) {
            if (userService.existsByUsername(usernameField.getValue())) {
                Notification.show("Этот логин уже занят", 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            AppUser appUser = userService.createUser(
                    usernameField.getValue(),
                    passwordField.getValue(),
                    roleComboBox.getValue()
            );

            Notification.show("Регистрация успешна!", 3000,
                            Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } else {
            Notification.show("Проверьте правильность данных", 3000,
                            Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}