package os.hw1.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class work{
    @Override
    public String toString() {
        return "work{" +
                ", id=" + id +
                ", functions=" + functions +
                ", input=" + input +
                ", s='" + s + '\'' +
                ", time=" + time +
                '}';
    }

    private clientRequestHandler crh;


    public int lastF(){
        return this.getFunctions().get(this.getFunctions().size()-1);
    }

    public clientRequestHandler getCrh() {
        return crh;
    }

    public void setCrh(clientRequestHandler crh) {
        this.crh = crh;
    }

    private int id;
    private List<Integer> functions;
    private int input;
    public String s;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public work(String s, clientRequestHandler clientRequestHandler, long time) {
        this.crh=clientRequestHandler;
        this.s=s;
        this.id=makeId();
        this.time=time;

        functions=new ArrayList<>();
//        s=str.replaceAll()
        String[] temp= s.split("\\|");

        for (int i = 0; i < temp.length-1; i++) {
            functions.add(Integer.valueOf(temp[i]));
        }
        String[] temp2=temp[temp.length-1].split(" ");
        functions.add(Integer.parseInt(temp2[0]));
        input=Integer.parseInt(temp2[1]);
    }
    public work(String s, clientRequestHandler clientRequestHandler, long time,int id,List<Integer> functions,int input) {
        this.crh=clientRequestHandler;
        this.s=s;
        this.time=time;
        this.id=id;
        this.functions=functions;
        this.input=input;
    }
//    public work makeW(){
//
//    }

    public void updateWork(int result){
        int t=functions.get(functions.size()-1);
        this.functions.remove(functions.size()-1);
        String f="|"+String.valueOf(t)+" "+String.valueOf(this.input);

        String temp=this.s.replace(f," "+String.valueOf(result));
        this.s=temp;
        this.input=result;
    }
    private int makeId(){
        Random rand=new Random();
        return rand.nextInt(10000);
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

}
