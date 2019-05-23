package summit.baseproject.view.test.db.vo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserVO extends RealmObject {

    @PrimaryKey
    private String id;
    private String pw;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }



}

