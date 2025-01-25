package org.vaadin.example.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.example.domain.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestFormReadOnly extends VerticalLayout {

    private final Text status = new Text("");
    private final Text data = new Text("");
    private final Text comment = new Text("");
    private final Text createdAt = new Text("");
    private final Text updatedAt = new Text("");

    private Button closeButton = new Button("Закрыть");

    public RequestFormReadOnly(Request request) {
        status.setText("Статус: " + request.getStatus());
        data.setText("Data: " + request.getData());
        comment.setText("Комментарий: " + (request.getComment() == null ? "Нет" : request.getComment()));
        createdAt.setText("Создано: " + formatDateTime(request.getCreatedAt()));
        updatedAt.setText("Обновлено: " + formatDateTime(request.getUpdatedAt()));

        closeButton.addClickListener(e -> closeDialog());

        add(new Div(status), new Div(data), new Div(comment),
                new Div(createdAt), new Div(updatedAt), closeButton);

        setPadding(true);
        setSpacing(true);
    }

    public void openInModalDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setCloseOnOutsideClick(false);
        dialog.open();
    }

    private void closeDialog() {
        if (getParent().isPresent() && getParent().get() instanceof Dialog dialog) {
            dialog.close();
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
