package org.vaadin.example.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Request;
import org.vaadin.example.security.SecurityService;
import org.vaadin.example.service.RequestService;

public class RequestFormOperator extends VerticalLayout {

    private final RequestService requestService;
    private final SecurityService securityService;
    private final Request request;

    private TextArea commentField = new TextArea("Комментарий");
    private Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create());
    private Button errorButton = new Button("Есть замечания", VaadinIcon.WARNING.create());
    private Button doneButton = new Button("Выполнить", VaadinIcon.CHECK.create());

    public RequestFormOperator(RequestService requestService,
                               SecurityService securityService,
                               Request request) {
        this.requestService = requestService;
        this.securityService = securityService;
        this.request = request;

        configureForm();
        setupLayout();
        addValidations();
    }

    private void configureForm() {
        setWidth("500px");
        setPadding(true);
        setSpacing(true);

        commentField.setWidthFull();
        commentField.setMinHeight("150px");
        commentField.setRequiredIndicatorVisible(true);
    }

    private void setupLayout() {
        H3 title = new H3("Обработка заявки #" + request.getId());
        title.getStyle().set("margin-top", "0");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, errorButton, doneButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.setSpacing(true);

        add(title, commentField, buttons);
    }

    private void addValidations() {
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        errorButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        doneButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        cancelButton.addClickListener(e -> closeDialog());
        errorButton.addClickListener(e -> validateAndMarkError());
        doneButton.addClickListener(e -> markDone());
    }

    private void validateAndMarkError() {
        if (commentField.isEmpty()) {
            commentField.setErrorMessage("Обязательное поле при отправке на доработку");
            commentField.setInvalid(true);
            Notification.show("Заполните комментарий", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        markError();
    }

    private void markError() {
        try {
            AppUser operator = securityService.getAuthenticatedUser();
            requestService.markError(request, commentField.getValue(), operator);
            Notification.show("Заявка отправлена на доработку", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeDialog();
        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void markDone() {
        try {
            AppUser operator = securityService.getAuthenticatedUser();
            requestService.markDone(request, operator);
            Notification.show("Заявка выполнена", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeDialog();
        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void openInModalDialog(Runnable onClose) {
        Dialog dialog = new Dialog(this);
        dialog.setCloseOnOutsideClick(false);
        dialog.addDialogCloseActionListener(e -> onClose.run());
        dialog.open();
    }

    private void closeDialog() {
        getParent().ifPresent(parent -> {
            if (parent instanceof Dialog dialog) {
                dialog.close();
            }
        });
    }
}