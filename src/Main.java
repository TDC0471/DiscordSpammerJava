import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {




    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {


        Scanner scanner = new Scanner(System.in);


        ArrayList<String> bot_names = new ArrayList<String>();
        ArrayList<String> bot_tokens = new ArrayList<String>();

        while(true) {
            System.out.println("Bot Name:");
            bot_names.add(scanner.nextLine());
            System.out.println("Bot Token:");
            bot_tokens.add(scanner.nextLine());
            System.out.println("More bots? (y/n):");
            if(scanner.nextLine().toLowerCase().toCharArray()[0] == 'n') break;
        }

        System.out.println("Timeout (Millis):");
        int timeout = Integer.parseInt(scanner.nextLine());

        ArrayList<String> channel_ids = new ArrayList<String>();
        ArrayList<Bot> Bots = new ArrayList<Bot>();

        while(true)
        {
            System.out.println("Channel ID:");
            channel_ids.add(scanner.nextLine());

            System.out.println("More inputs? (y/n)");
            if(scanner.nextLine().toLowerCase().toCharArray()[0] == 'n') break;
        }


        for(int i = 0; i < bot_names.size(); i++)
        {
            Bots.add(new Bot(bot_names.get(i), bot_tokens.get(i)));
        }



        ThreadRun tr = new ThreadRun(channel_ids, Bots, timeout);
        tr.run();


    }






}


class ThreadRun extends java.lang.Thread
{

    ArrayList<String> channel_ids = new ArrayList<String>();
    ArrayList<Bot> Bots = new ArrayList<Bot>();
    ArrayList<String> msgs = readFileLines("msg.txt");
    int timeout;

    public ThreadRun(ArrayList<String> channel_ids, ArrayList<Bot> Bots, int timeout) throws IOException {
        this.channel_ids = channel_ids;
        this.Bots = Bots;
        this.timeout = timeout;
    }


    public void run() {
        for (String msg: msgs) {
            try {
                sleep(timeout);
            } catch (InterruptedException e) {
                run();
                throw new RuntimeException(e);
            }
            for(String id: channel_ids)
            {
                for(Bot bot: Bots)
                {
                    try {
                        Send(bot.botName, msg, bot.token, id);
                    } catch (IOException e) {
                        run();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        run();
    }

    private static ArrayList<String> readFileLines(String filepath) throws FileNotFoundException, IOException{
        File fp = new File(filepath);
        FileReader fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<String> lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) { lines.add(line); }

        fr.close();
        return lines;
    }

     void Send(String botName, String msg, String token, String channelID) throws IOException {
        URL url = new URL("https://discordapp.com/api/v9/channels/" + channelID + "/messages");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST"); // PUT is another valid option

        byte[] out = ("{\"content\":\"" + msg + "\"}").getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setRequestProperty("Authorization", token);
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }

}

class Bot
{
    Bot(String botName, String token)
    {
        this.botName = botName;
        this.token = token;
    }
    public String botName;
    public String token;
}
