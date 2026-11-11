package dataset.model;

import lombok.Data;

import java.util.Date;

@Data
public class DataStatistics {

    private String deptName;

    private Long deptId;

    private Integer compiledCatalogTotal;

    private Integer claimedCatalogTotal;

    private String claimRatio;

    private Integer completedCompilation;

    private Integer pendingCompilation;

    private String compilationRatio;


    private String compilationProgress;

    private String   type;

    private String level;

    private Date createDate;
}
