package com.swp.springboot.modal.bo;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.modal.bo
 * @Author: Siwanper
 * @CreateDate: 2018/11/9 下午9:50
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
public class BackupResponseBo {

    private String attachPath;
    private String themePath;
    private String sqlPath;

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public String getThemePath() {
        return themePath;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    public String getSqlPath() {
        return sqlPath;
    }

    public void setSqlPath(String sqlPath) {
        this.sqlPath = sqlPath;
    }
}
