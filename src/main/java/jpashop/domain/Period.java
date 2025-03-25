package jpashop.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Period {

    private LocalDateTime startDt;
    private LocalDateTime endDt;

    public Period() {
    }

    public Period(LocalDateTime startDt, LocalDateTime endDt) {
        this.startDt = startDt;
        this.endDt = endDt;
    }

    public LocalDateTime getStartDt() {
        return startDt;
    }

    public void setStartDt(LocalDateTime startDt) {
        this.startDt = startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDateTime endDt) {
        this.endDt = endDt;
    }
}
