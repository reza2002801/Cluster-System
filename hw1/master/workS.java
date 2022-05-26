package os.hw1.master;

import java.io.Serializable;
import java.util.List;

public class workS  implements Serializable {
    private int id;
    private List<Integer> functions;
    private int input;
    public String s;
    private long time;
    public workS(work work) {
        this.id=work.getId();
        this.functions=work.getFunctions();
        this.input=work.getInput();
        this.s=work.getS();
        this.time=work.getTime();
    }
    public void updateWork(int result){
        int t=functions.get(functions.size()-1);
        this.functions.remove(functions.size()-1);
        String f="|"+String.valueOf(t)+" "+String.valueOf(this.input);

        String temp=this.s.replace(f," "+String.valueOf(result));
        this.s=temp;
        this.input=result;
    }

    @Override
    public String toString() {
        return "workS{" +
                "id=" + id +
                ", functions=" + functions +
                ", input=" + input +
                ", s='" + s + '\'' +
                ", time=" + time +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Integer> functions) {
        this.functions = functions;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
