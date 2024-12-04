package mk.finki.ukim.mk.daswebapplication.service;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

@Service
public class CodesService {

    public Set<String> getCodes() throws FileNotFoundException {
        Set<String> codes = new HashSet<String>();
        String path = "/DASProject/stock_data.csv";
        Reader in = new FileReader(path);
        return null;
    }
}

