package edu.calpoly.robot;

public class RobotMessage {
    public float[] jointAngles = new float[6];
    public float[] position = new float[3];

    RobotMessage(RobotMessage rm){
        jointAngles = rm.jointAngles.clone();
        position = rm.position.clone();
    }

    RobotMessage(String message){
        // expect: "ROBOT,J1,J2,J3,J4,J5,J6,X,Y,Z"
        if (!message.startsWith("ROBOT")){
            throw new Error("message is not ROBOT.");
        }

        String[] parts = message.split(",");
        // expect: [ROBOT, J1, J2, J3, J4, J5, J6, X, Y, Z]
        if (parts.length != 10){
            throw new Error("ROBOT message has unexpected amount of data. Expected 10 parts, recieved " + parts.length + ".");
        }

        // Parse joint angles.
        for (int i=0; i<6; ++i){
            jointAngles[i] = Float.parseFloat(parts[i+1]);
        }
        // Parse position.
        for (int i=0; i<3; ++i){
            position[i] = Float.parseFloat(parts[i+7]);
        }
    }

    public RobotMessage clone(){
        return new RobotMessage(this); 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ROBOT:\n\tAngles: ");
        for (float angle : jointAngles) sb.append(angle + ",");
        sb.append("\n\tPosition:");
        sb.append(" x=" + position[0] + ", y=" + position[1] + ", z=" + position[2] + "\n");
        return sb.toString();
    }
    
}
