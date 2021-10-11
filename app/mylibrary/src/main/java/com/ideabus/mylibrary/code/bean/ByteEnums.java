package com.ideabus.mylibrary.code.bean;

import static com.ideabus.mylibrary.code.tools.Utils.contains;

public class ByteEnums {
    public enum FirstByteCommands {

        REALTIME_DATA(-21),
        DEVICE_VERSION(-14),
        FIVE_MIN_STEPS_DATA(-29),

        DATA_STORAGE_INFO_DATA(-32),
        DATA_STORAGE_INFO_DATA2(-17),

        SPO2_PIECE_OF_DATA(-31),
        DAY_STEPS_PIECE_OF_DATA(-30),
        DAY_STEPS_WITH_TARGET_CALORIE_PIECE_OF_DATA(-22),
        FIVE_MIN_STEPS_PIECE_OF_DATA(-28),

        SPO2_PIECE_OF_DATA_CHECK(-20),

        ORIGINAL_CODE_OR_ORDINARY_DATA(-19),

        READY_FOR_TRANSFERRING_2(-48),
        PIECE_OF_DATA_DELETED_2(-47),
        ON_PR_DATA_PIECE_2(-46),
        ON_SPO2_DATA_PIECE_2(-45),
        ON_PI_DATA_PIECE_2(-41),

        CURRENT_SYS_CONFIGURATION_OPERATION_STATE(-1),
        CURRENT_STORAGE_STATE(-2),

        COMMAND_NOT_SUPPORTED(-16),
        CURRENT_DATE_TIME_REQUEST(-15),
        DEVICE_VERSION_REQUEST(-13),

        SET_WEIGHT_RESPONSE(-11, -12),
        SET_HEIGHT_RESPONSE(-6),
        SET_CALORIE_RESPONSE(-5);

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
