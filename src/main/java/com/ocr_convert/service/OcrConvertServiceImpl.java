package com.ocr_convert.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OcrConvertServiceImpl implements OcrConvertService {

    @Override
    public String getConvertResult(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            JSONArray fields = json.getJSONArray("images").getJSONObject(0).getJSONArray("fields");

            double[] y = {0, 0, 0, 0};
            int sameCnt = 0;
            StringBuilder convetString = new StringBuilder();

            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                JSONObject boundingPoly = field.getJSONObject("boundingPoly");
                JSONArray vertices = boundingPoly.getJSONArray("vertices");
                double inferConfidence = field.getDouble("inferConfidence");
                String inferText = field.getString("inferText");

                if (inferConfidence > 0.9) {
                    if (y[0] == 0) {
                        y[0] = vertices.getJSONObject(0).getDouble("y");
                        y[1] = vertices.getJSONObject(1).getDouble("y");
                        y[2] = vertices.getJSONObject(2).getDouble("y");
                        y[3] = vertices.getJSONObject(3).getDouble("y");
                        convetString.append(inferText).append(" ");
                        continue;
                    }
                    for (int j = 0; j < vertices.length(); j++) {
                        if (y[j] == vertices.getJSONObject(0).getDouble("y")) {
                            sameCnt++;
                        }
                        y[j] = vertices.getJSONObject(0).getDouble("y");
                    }

                    if (sameCnt >= 2) {
                        convetString.append(inferText).append(" ");
                    } else {
                        convetString.append("\n").append(inferText).append(" ");
                    }

                    sameCnt = 0;
                }
            }

            return convetString.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: 테이블이 아닌 평문 컨버팅 로직 필요
    @Override
    public String getTableConvertResult(String payload) {

        try {
            JSONObject json = new JSONObject(payload);
            JSONArray tables = json.getJSONArray("images").getJSONObject(0).getJSONArray("tables");
            StringBuilder convetString = new StringBuilder();

            for (int k = 0; k < tables.length(); k++) {

                JSONArray cells = tables.getJSONObject(k).getJSONArray("cells");
                for (int i = 0; i < cells.length(); i++) {
                    JSONArray cellTextLines = cells.getJSONObject(i).getJSONArray("cellTextLines");
                    if (cellTextLines.length() == 0) {
                        continue;
                    }

                    JSONArray cellWords = cellTextLines.getJSONObject(0).getJSONArray("cellWords");
                    for (int j = 0; j < cellWords.length(); j++) {
                        convetString.append(cellWords.getJSONObject(j).getString("inferText"))
                            .append(" ");
                    }
                    convetString.append("\n");
                }
            }
            return convetString.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
