package os.hw1.master;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MasterMain {

    static List<Integer> programweights;
static List<Process> dc=new ArrayList<>();
    static int maxW;
    public static void main(String[] args) throws IOException, InterruptedException {
        // get inputs
//        Logger.log("Files in Java might be tricky, but it is fun enough!");
//        FileWriter myWriter = new FileWriter("filename.txt");
//        myWriter.write("Files in Java might be tricky, but it is fun enough!");
//        myWriter.close();
        Scanner scanner = new Scanner(System.in);
        int portNumber = Integer.parseInt(scanner.nextLine());

        int workerNum=Integer.parseInt(scanner.nextLine());
        int maxWeight=Integer.parseInt(scanner.nextLine());
        int argNum=Integer.parseInt(scanner.nextLine());
        maxW=maxWeight;
        List<String> arguments=new ArrayList<String>();
        for (int i = 0; i < argNum; i++) {
            arguments.add(scanner.nextLine());
        }
//        List<String> arguments2=arguments;
//        arguments2.set(2,"C:\\Users\\NoteBook TANDIS\\intelijProjects\\OsProject1\\out\\production\\OsProject1");

        int programNum=Integer.parseInt(scanner.nextLine());
        programweights=new ArrayList<>();
        List<String> programs=new ArrayList<String>();
        List<String> programWeights=new ArrayList<String>();
        for (int i = 0; i < programNum; i++) {
            String temp=scanner.nextLine();
            String[] a=temp.split(" ",2);
            programs.add(a[0]);
            programWeights.add(a[1]);
            programweights.add(Integer.parseInt(a[1]));
        }





        // start cache server
        List<String> p=new ArrayList<>();
        p.add(String.valueOf(9500));
        ProcessBuilder b=exec(CacheServer.class, null,p);
        Process c=b.start();


//run main server
        MainServer coordinator = new MainServer(9000);
        coordinator.start();


        clientNetworker server = new clientNetworker();
        clientNetworker.port=portNumber;
        server.start();

        System.out.println("master start "+ ProcessHandle.current().pid()+" "+String.valueOf(9500));
        System.out.println("cache start "+c.pid()+" "+String.valueOf(9500));
//        System.out.println(p);



// start Workers

        for (int i = 0; i < workerNum; i++) {
            p=makeArgs(arguments,programs,programWeights);
            b=exec(Worker.class, null, p);
            c=b.start();

            dc.add(c);
            System.out.println("worker "+ String.valueOf(i)+ " start "+c.pid()+" "+String.valueOf(9000));
        }



        // start clinet handler



    }
    static List<String> makeArgs(List<String> arguments,List<String> programs ,List<String> programWeights){

        List<String> p=new ArrayList<>();
        p.add(String.valueOf(arguments.size()));
        p.addAll(arguments);
        p.add(String.valueOf(programs.size()));
        p.addAll(programs);
        p.add(String.valueOf(programWeights.size()));
        p.addAll(programWeights);
        p.add(String.valueOf(9000));
        return p;
    }
    public static ProcessBuilder exec(Class clazz, List<String> jvmArgs, List<String> args) {
        String javaHome = System.getProperty("java.home");

        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        String className = clazz.getName();

        List<String> command = new ArrayList<>();
        command.add(javaBin);
//        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);

        return new ProcessBuilder(command);
    }
}
