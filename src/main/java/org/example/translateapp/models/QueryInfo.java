package org.example.translateapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryInfo {

    private int id;

    private String userIp;

    private String input;

    private String output;

    private Date datetime;

}