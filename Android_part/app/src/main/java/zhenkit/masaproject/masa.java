package zhenkit.masaproject;

public class masa {
    public String temperature;
    public String heartRate;
    public String fall;

    public masa(){
    }
    public masa(String temperature,String heartRate,String fall){
        this.temperature=temperature;
        this.heartRate=heartRate;
        this.heartRate=fall;
    }
    public String getTemperature(){
        return temperature;
    }
    public String getHeartRate(){
        return heartRate;
    }
    public String getFall(){
        return fall;
    }
}
