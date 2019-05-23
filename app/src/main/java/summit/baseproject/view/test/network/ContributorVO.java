package summit.baseproject.view.test.network;

public class ContributorVO {

    String login;
    int contributions;

    @Override
    public String toString() {
        return login + " (" + contributions + ")";
    }
}