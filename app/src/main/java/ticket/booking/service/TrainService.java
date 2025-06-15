package ticket.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    ObjectMapper objectMapper=new ObjectMapper();
    List<Train> trainList;
    private static final String TRAIN_DB_PATH = "../localDB/trains.json";
    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream().filter(train -> validTrain(train,source,destination)).collect(Collectors.toList());
    }
    public Boolean validTrain(Train train,String source,String destination){
        List<String> stationPos=train.getStations();
        int sourceIndex=stationPos.indexOf(source.toLowerCase());
        int destinationIndex=stationPos.indexOf(destination.toLowerCase());
        return sourceIndex!=-1 && destinationIndex!=-1 && sourceIndex<destinationIndex;
    }

    public void addTrain(Train newTrain) throws IOException {
        // Check if a train with the same trainId already exists
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            // If a train with the same trainId exists, update it instead of adding a new one
            updateTrain(newTrain);
        } else {
            // Otherwise, add the new train to the list
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) throws IOException {
        // Find the index of the train with the same trainId
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // If found, replace the existing train with the updated one
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            // If not found, treat it as adding a new train
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() throws IOException {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception based on your application's requirements
        }
    }
}
