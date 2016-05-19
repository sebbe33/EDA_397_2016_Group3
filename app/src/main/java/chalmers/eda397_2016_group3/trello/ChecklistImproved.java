package chalmers.eda397_2016_group3.trello;

import org.trello4j.model.TrelloObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianblomberg on 2016-05-19.
 */
public class ChecklistImproved extends TrelloObject {

    private String name;

    private String idBoard;

    private java.util.List<CheckItem> checkItems = new ArrayList<CheckItem>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdBoard() {
        return idBoard;
    }

    public void setIdBoard(String idBoard) {
        this.idBoard = idBoard;
    }

    public List<CheckItem> getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(List<CheckItem> checkItems) {
        this.checkItems = checkItems;
    }

    public class CheckItem extends TrelloObject {

        private String name;
        private String type;
        private double pos;
        private boolean checked = false;
        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getPos() {
            return pos;
        }

        public void setPos(double pos) {
            this.pos = pos;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean isChecked) {
            this.checked = isChecked;
        }
    }
}
