package ink.helloworld.halo.model.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 *     访问记录
 * </pre>
 *
 * @author : Gallin
 * @date : 2019/03/22
 */
@Data
@Entity
@Table(name = "halo_visits")
@EntityListeners(AuditingEntityListener.class)
public class Visits implements Serializable {

    private static final long serialVersionUID = -2571815432301283171L;

    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long visitId;

    /**
     * 产生日志的ip
     */
    private String visitIp;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 用户代理
     */
    private String visitUserAgent;

    /**
     * 产生的时间
     */
    @CreatedDate
    private Date visitTime;

    public Visits() {
    }

}
