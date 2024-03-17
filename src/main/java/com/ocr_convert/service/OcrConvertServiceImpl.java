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
            StringBuilder convertString = new StringBuilder();

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
                        convertString.append(inferText).append(" ");
                        continue;
                    }
                    for (int j = 0; j < vertices.length(); j++) {
                        if (y[j] == vertices.getJSONObject(0).getDouble("y")) {
                            sameCnt++;
                        }
                        y[j] = vertices.getJSONObject(0).getDouble("y");
                    }

                    if (sameCnt >= 2) {
                        convertString.append(inferText).append(" ");
                    } else {
                        convertString.append("\n").append(inferText).append(" ");
                    }

                    sameCnt = 0;
                }
            }

            return convertString.toString();
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
            StringBuilder convertString = new StringBuilder();

            for (int i = 0; i < tables.length(); i++) {

                JSONArray cells = tables.getJSONObject(i).getJSONArray("cells");
                for (int j = 0; j < cells.length(); j++) {
                    JSONArray cellTextLines = cells.getJSONObject(j).getJSONArray("cellTextLines");
                    if (cellTextLines.length() == 0) {
                        continue;
                    }

                    for (int k = 0; k < cellTextLines.length(); k++) {
                        JSONArray cellWords = cellTextLines.getJSONObject(k)
                            .getJSONArray("cellWords");
                        for (int x = 0; x < cellWords.length(); x++) {
                            convertString.append(cellWords.getJSONObject(x).getString("inferText"))
                                .append(" ");
                        }
                        convertString.append("\n");
                    }

                    convertString.append("\n")
                        .append("----------------------------")
                        .append("\n");
                }
            }
            return convertString.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
