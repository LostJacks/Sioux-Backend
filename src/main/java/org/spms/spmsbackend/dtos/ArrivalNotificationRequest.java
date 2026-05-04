package org.spms.spmsbackend.dtos;

import java.util.List;

/**
 * Optional request body for {@code POST /api/orders/{id}/arrival-notification}.
 * The legacy callers (the existing "Test Arrival Notification" buttons in
 * the frontend) post no body at all, which is fine — both fields are
 * optional.
 *
 * <p>{@code targetUserIds} lets the receptionist pick which managers receive
 * a Sioux/unaddressed package alert. If null or empty, the notification
 * fans out to every manager in the system.</p>
 */
public class ArrivalNotificationRequest {

    private List<Integer> targetUserIds;

    public ArrivalNotificationRequest() {
    }

    public List<Integer> getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(List<Integer> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }
}
