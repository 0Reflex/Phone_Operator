/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OS;

/**
 *
 * @author Emine
 */
public class OpLady {
   private int id;
    private boolean busy;

    public OpLady(int id) {
        this.id = id;
        this.busy = false;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return !busy;
    }

    public  void setBusy(boolean busy) {
        this.busy = busy;
    }
}



