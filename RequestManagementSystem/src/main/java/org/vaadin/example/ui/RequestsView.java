package org.vaadin.example.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.vaadin.example.broadcast.Broadcaster;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Request;
import org.vaadin.example.domain.Role;
import org.vaadin.example.domain.Status;
import org.vaadin.example.security.SecurityService;
import org.vaadin.example.service.RequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
public class RequestsView extends VerticalLayout implements BeforeEnterObserver {

    private final RequestService requestService;
    private final SecurityService securityService;

    public RequestsView(RequestService requestService, SecurityService securityService) {
        this.requestService = requestService;
        this.securityService = securityService;
    }

    private Grid<Request> grid = new Grid<>(Request.class, false);

    private Button createButton = new Button("Создать заявку");

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!securityService.isLoggedIn()) {
            beforeEnterEvent.forwardTo(LoginView.class);
            return;
        }
        initView();
    }

    private void initView() {
        configureGrid();
        configureCreateButton();
        add(createButton, grid);
        refreshGrid();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Broadcaster.register(this::handleBroadcast);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        Broadcaster.unregister(this::handleBroadcast);
        super.onDetach(detachEvent);
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(Request::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(r -> r.getClient().getName()).setHeader("Клиент").setSortable(true);
        grid.addColumn(r -> Optional.ofNullable(r.getOperator())
                .map(AppUser::getName).orElse("")).setHeader("Оператор");
        grid.addComponentColumn(this::createStatusBadge).setHeader("Статус");
        grid.addColumn(Request::getData).setHeader("Data").setFlexGrow(2);
        grid.addColumn(Request::getComment).setHeader("Комментарий");
        grid.addColumn(r -> formatDateTime(r.getCreatedAt())).setHeader("Создано");
        grid.addColumn(r -> formatDateTime(r.getUpdatedAt())).setHeader("Обновлено");
        grid.addItemClickListener(event -> {
            if (event.getClickCount() == 1) {
                handleRequestClick(event.getItem());
            }
        });
        grid.setClassNameGenerator(request -> "clickable-row");
        grid.setPageSize(20);
        grid.setMultiSort(true);
        grid.setHeight("70vh");
        UI.getCurrent().getPage().addStyleSheet(
            "data:text/css," +
                    ".clickable-row:hover {" +
                    "  background-color: #f5f5f5;" +
                    "  cursor: pointer;" +
                    "}"
        );
    }

    private void handleRequestClick(Request request) {
        AppUser currentAppUser = securityService.getAuthenticatedUser();

        if (currentAppUser.getRole() == Role.CLIENT) {
            if (request.getStatus() == Status.ERROR && request.getClient().getId().equals(currentAppUser.getId())) {
                RequestFormClient form = new RequestFormClient(requestService, securityService, request);
                form.openInModalDialog(this::refreshGrid);
            } else {
                requestService.viewRequest(request);

                RequestFormReadOnly readOnlyForm = new RequestFormReadOnly(request);
                readOnlyForm.openInModalDialog();
            }

        } else {
            if (request.getStatus() == Status.NEW || request.getStatus() == Status.FIXED) {
                RequestFormOperator form = new RequestFormOperator(requestService, securityService, request);
                form.openInModalDialog(this::refreshGrid);
            } else {
                showStatusDialog(request);
            }
        }
    }

    private void configureCreateButton() {
        AppUser currentAppUser = securityService.getAuthenticatedUser();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        if (currentAppUser.getRole() == Role.CLIENT) {
            createButton.setVisible(true);
            createButton.addClickListener(e -> {
                RequestFormClient form = new RequestFormClient(requestService, securityService);
                form.openInModalDialog(this::refreshGrid);
            });
        } else {
            createButton.setVisible(false);
        }
    }

    private void showStatusDialog(Request request) {
        com.vaadin.flow.component.notification.Notification.show(
                "Статус заявки: " + request.getStatus()
        );
    }

    private void refreshGrid() {
        AppUser currentAppUser = securityService.getAuthenticatedUser();
        List<Request> requests;
        if (currentAppUser.getRole() == Role.CLIENT) {
            requests = requestService.findAllByClient(currentAppUser);
        } else {
            requests = requestService.findAll();
        }
        grid.setItems(requests);
        grid.getDataProvider().refreshAll();
    }

    private Component createStatusBadge(Request request) {
        Span badge = new Span(request.getStatus().name());
        badge.getElement().getThemeList().add("badge " + getStatusTheme(request.getStatus()));
        return badge;
    }

    private String getStatusTheme(Status status) {
        return switch (status) {
            case NEW -> "primary";
            case FIXED -> "success";
            case ERROR -> "error";
            case DONE -> "contrast";
        };
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private void handleBroadcast() {
        getUI().ifPresent(ui -> ui.access(() -> {
            refreshGrid();
            grid.getDataProvider().refreshAll();
        }));
    }

}