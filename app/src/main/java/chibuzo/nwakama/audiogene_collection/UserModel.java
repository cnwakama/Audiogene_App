package chibuzo.nwakama.audiogene_collection;

import android.view.View;

/**
 * Created by arthonsystechnologiesllp on 10/03/17.
 * https://github.com/DharmbirChoudhary/Listview-With-Single-Item-Selection/blob/master/app/src/main/java/com/multiselect/UserModel.java
 */

public class UserModel{


    //boolean isSelected;
    //boolean isSelectedText;
    String userName;

    //now create constructor and getter setter method using shortcut like command+n for mac & Alt+Insert for window.


    public UserModel( String userName){//, boolean isSelected){//, boolean isSelected2) {
        //this.isSelected = isSelected;
        this.userName = userName;
        //this.isSelectedText = isSelected2;
    }

    /**public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
    }*/

    /**public boolean[] isSelected() {
        boolean[] isSelected =  {isSelectedText, isSelectedImage};
        return isSelected;
    }*/

    /**public void setSelectedImage(boolean selected) {
        isSelectedImage = selected;
    }

    public void setSelectedText(boolean selected) {
        isSelectedText = selected;
    }*/

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
