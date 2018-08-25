package download;

public class Server {

    public static void main(String[] args) {
        for (int i = 1; i <= 388; i++) {
            String downloadURL = "https://ip92737063.ahcdn.com/key=A0SvKJX9J8dH8rHxYG8DaQ,s=,end=1535218334/state=LT9h/reftag=057661720/media=hls/ssd4/121/2/32053212.mp4/seg-"
                            + i + "-v1-a1.ts";
            System.out.println(downloadURL);
        }
    }
}
