package com.ideabus.mylibrary.code.bean;

import static com.ideabus.mylibrary.code.tools.Utils.contains;

public class ByteEnums {
    public enum FirstByteCommands {

        REALTIME_DATA(-21),
        INPUT_DATA_CONSTANT(-14),
        FIVE_MIN_STEPS_DATA(-29),
        DATA_STORAGE_INFO_DATA(-32),

        SPO2_PIECE_OF_DATA(-31),
        DAY_STEPS_PIECE_OF_DATA(-30),
        DAY_STEPS_WITH_TARGET_CALORIE_PIECE_OF_DATA(-22),
        FIVE_MIN_STEPS_PIECE_OF_DATA(-28),

        CURRENT_OPERATION_STATUS(-1),

        COMMAND_NOT_SUPPORTED(-16),
        CURRENT_DATE_TIME_REQUEST(-15),
        READY_FOR_REALTIME(-13),

        SET_WEIGHT_RESPONSE(-11, -12),
        SET_HEIGHT_RESPONSE(-6),
        SET_CALORIE_RESPONSE(-5),

        TODO(-2, -17, -20, -19, -48, -47, -46, -45, -41);

        //-----------pattern code-----------------
        private final Integer[] cmdBytes;
        FirstByteCommands(Integer... cmdBytes) {
            this.cmdBytes = cmdBytes;
        }
        public static FirstByteCommands getByCmdByte(int cmdByte) {
            for (FirstByteCommands command:
                    FirstByteCommands.values()) {
                if (contains(command.cmdBytes, cmdByte))
                    return command;
            }
            return null;
        }
        //-----------pattern code-----------------
    }

    enum SecondByteRealtimeData {

        WAVE(0), REALTIME_DATA(1), REALTIME_END(127);

        //-----------pattern code-----------------
        private final Integer[] cmdBytes;
        SecondByteRealtimeData(Integer... cmdBytes) {
            this.cmdBytes = cmdBytes;
        }
        public static SecondByteRealtimeData getByCmdByte(int cmdByte) {
            for (SecondByteRealtimeData command:
                    SecondByteRealtimeData.values()) {
                if (contains(command.cmdBytes, cmdByte))
                    return command;
            }
            return null;
        }
        //-----------pattern code-----------------
    }

    enum SecondByteStatus {

        SUCCESS(0), FAIL(1);

        //-----------pattern code-----------------
        private final Integer[] cmdBytes;
        SecondByteStatus(Integer... cmdBytes) {
            this.cmdBytes = cmdBytes;
        }
        public static SecondByteStatus getByCmdByte(int cmdByte) {
            for (SecondByteStatus command:
                    SecondByteStatus.values()) {
                if (contains(command.cmdBytes, cmdByte))
                    return command;
            }
            return null;
        }
        //-----------pattern code-----------------
    }
}
