package org.vaadin.example.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.vaadin.example.broadcast.Broadcaster;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Request;
import org.vaadin.example.domain.Status;
import org.vaadin.example.security.SecurityService;
import org.vaadin.example.service.RequestService;

public class RequestFormClient extends VerticalLayout {

    private final RequestService requestService;
    private final SecurityService securityService;
    private Request request;

    private TextArea dataField = new TextArea("Data");
    private Span commentText = new Span();

    private Button cancelButton = new Button("Отмена");
    private Button saveButton = new Button("Сохранить");

    public RequestFormClient(RequestService requestService, SecurityService securityService) {
        this(requestService, securityService, null);
    }

    public RequestFormClient(RequestService requestService,
                             SecurityService securityService,
                             Request request) {
        this.requestService = requestService;
        this.securityService = securityService;
        this.request = request;

        configureForm();
        setupLayout();
        addValidation();
    }

    private void configureForm() {
        setWidth("500px");
        setPadding(true);
        setSpacing(true);

        if (request != null && request.getStatus() == Status.ERROR) {
            commentText.setText("Комментарий оператора: " + request.getComment());
            dataField.setValue(request.getData() != null ? request.getData() : "");
        } else {
            commentText.setVisible(false);
        }

        dataField.setWidthFull();
        dataField.setMinHeight("150px");
    }

    private void setupLayout() {
        H3 title = new H3(request != null ? "Редактирование заявки" : "Новая заявка");
        title.getStyle().set("margin-top", "0");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.setSpacing(true);

        add(title, commentText, dataField, buttons);
    }

    private void addValidation() {
        dataField.setRequired(true);
        dataField.setErrorMessage("Поле обязательно для заполнения");

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener(e -> closeDialog());
        saveButton.addClickListener(e -> saveAction());
    }

    private void saveAction() {
        if (dataField.isInvalid() || dataField.isEmpty()) {
            Notification.show("Заполните обязательные поля", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            AppUser appUser = securityService.getAuthenticatedUser();
            if (request == null) {
                requestService.createRequest(appUser, dataField.getValue());
                Notification.show("Заявка успешно создана", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                requestService.markFixed(request, dataField.getValue());
                Notification.show("Заявка обновлена", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            closeDialog();
        } catch (Exception e) {
            Notification.show("Ошибка сохранения: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void openInModalDialog(Runnable onClose) {
        Dialog dialog = new Dialog(this);
        dialog.setCloseOnOutsideClick(false);
        dialog.addDialogCloseActionListener(e -> {
            onClose.run();
            Broadcaster.broadcast();
        });
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