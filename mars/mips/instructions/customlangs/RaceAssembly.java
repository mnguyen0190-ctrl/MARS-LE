package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;

public class RaceAssembly extends CustomAssembly {
    @Override
    public String getName() {
        return "Race Assembly";
    }

    @Override
    public String getDescription() {
        return "Assembly language themed after the racing genre";
    }

    @Override
    protected void populate() {

        //1. gain
        instructionList.add(
                new BasicInstruction("gain $t0,$t1,$t2",
                        "Gain (add): $t0 = $t1 + $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int sum = rs + rt;
                                RegisterFile.updateRegister(ops[0], sum);
                            }
                        }
                )
        );
        //2. lose
        instructionList.add(
                new BasicInstruction("lose $t0,$t1,$t2",
                        "Lose (sub): $t0 = $t1 + $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int diff = rs - rt;
                                RegisterFile.updateRegister(ops[0], diff);
                            }
                        }
                )
        );
        //3. put
        instructionList.add(
                new BasicInstruction("put $t0,$t1,-100",
                        "put (addi): $t0 = $t1 + imm",
                        BasicInstructionFormat.I_FORMAT,
                        "100000 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int imm = ops[2] << 16 >> 16;
                                int result = rs + imm;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        //4. weld
        instructionList.add(
                new BasicInstruction("weld $t0,$t1,$t2",
                        "Weld (AND): $t0 = $t1 & $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int result = rs & rt;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        //5. merge
        instructionList.add(
                new BasicInstruction("merge $t0,$t1,$t2",
                        "Merge (OR): $t0 = $t1 | $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int result = rs | rt;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        //6. versus
        instructionList.add(
                new BasicInstruction("vs $t0,$t1,$t2",
                        "Versus (compare): $t0 = 1 if ($t1 < $t2) else 0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int result = (rs < rt) ? 1 : 0;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        //7. Garage
        instructionList.add(
                new BasicInstruction("gre $t0,4($t1)",
                        "Garage (Store): memory[$t1 + imm] = $t0",
                        BasicInstructionFormat.I_FORMAT,
                        "100001 ttttt fffff ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();

                                int rt  = ops[0];
                                int rs  = ops[2];
                                int imm = ops[1] << 16 >> 16;
                                int addr = RegisterFile.getValue(rs) + imm;

                                try {
                                    Globals.memory.setWord(addr, RegisterFile.getValue(rt));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }
                )
        );

        //8. Drive
        instructionList.add(
                new BasicInstruction("dr $t0,4($t1)",
                        "Drive (Load): $t0 = memory[$t1 + imm]",
                        BasicInstructionFormat.I_FORMAT,
                        "100010 ttttt fffff ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int rt = ops[0];
                                int imm = ops[1] << 16 >> 16;
                                int rs = ops[2];

                                int address = RegisterFile.getValue(rs) + imm;
                                try {
                                    int value = Globals.memory.getWord(address);
                                    RegisterFile.updateRegister(rt, value);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }
                )
        );
        //9. Nitro
        instructionList.add(
                new BasicInstruction("nos $t0,$t1,$t2",
                        "Nitro (Multiply): $t0 = $t1 * $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000110",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                int result = rs * rt;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        //10. Split
        instructionList.add(
                new BasicInstruction("split $t0,$t1,$t2",
                        "Split (Divide): $t0 = $t1 / $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000111",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int rt = RegisterFile.getValue(ops[2]);
                                if (rt == 0) {
                                    throw new ProcessingException(statement, "division by zero", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                int result = rs / rt;
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        /*
         * UNIQUE INSTRUCTIONS
         */
        //1. Fuel up
        instructionList.add(
                new BasicInstruction("fuel $t0,100",
                        "Fuel up: $t0 = imm",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 fffff 00000 ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int imm = ops[1] << 16 >> 16;
                                RegisterFile.updateRegister(ops[0], imm);
                            }
                        }
                )
        );
        //2. Lap up
        instructionList.add(
                new BasicInstruction("lup $t0",
                        "Lap up: $t0 = $t0 + 1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 001000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int val = RegisterFile.getValue(ops[0]);
                                RegisterFile.updateRegister(ops[0], val + 1);
                            }
                        }
                )
        );
        //3. Check lap
        instructionList.add(
                new BasicInstruction("cklap $t0,$t1,label",
                        "Check lap: if ($t0 == $t1) branch to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "100100 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                if (RegisterFile.getValue(ops[0]) == RegisterFile.getValue(ops[1])) {
                                    Globals.instructionSet.processBranch(ops[2]);
                                }
                            }
                        }
                )
        );
        //4. Overtake
        instructionList.add(
                new BasicInstruction("otk $t0,$t1,label",
                        "Overtake: if ($t0 > $t1) branch to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "100101 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                if (RegisterFile.getValue(ops[0]) > RegisterFile.getValue(ops[1])) {
                                    Globals.instructionSet.processBranch(ops[2]);
                                }
                            }
                        }
                )
        );
        //5. Turbo
        instructionList.add(
                new BasicInstruction("turbo $t0,$t0",
                        "Turbo (double speed): $t0 = 2 * $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 001001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                RegisterFile.updateRegister(ops[0],rs + rs);
                            }
                        }
                )
        );
        //6. Skid
        instructionList.add(
                new BasicInstruction("skid $t2,$t0",
                        "Skid (return to zero if negative): max($t0, 0)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss fffff 00000 001010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int result = Math.max(rs, 0);
                                RegisterFile.updateRegister(ops[0], result);
                            }
                        }
                )
        );
        // 7. Pitstop
        instructionList.add(
                new BasicInstruction("pit $t0,$t1",
                        "Pitstop: swap values stored in two registers ($t0 <--> $t1) ",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss 00000 00000 001011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();

                                int rs = ops[0];
                                int rt = ops[1];

                                int rsVal = RegisterFile.getValue(rs);
                                int rtVal = RegisterFile.getValue(rt);

                                RegisterFile.updateRegister(ops[0], rtVal);
                                RegisterFile.updateRegister(ops[1], rsVal);
                            }
                        }
                )
        );
        //8. Hazard
        instructionList.add(
                new BasicInstruction("hz $t0,$t1,label",
                        "Hazard: if ($t0 != $t1) branch to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "100110 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[0]);
                                int rt = RegisterFile.getValue(ops[1]);

                                if (rs != rt) {
                                    Globals.instructionSet.processBranch(ops[2]);
                                }
                            }
                        }
                )
        );
        //9. Heat up
        instructionList.add(
                new BasicInstruction("hot $t0,$t,100",
                        "Heatup: $t0 = $t1 + imm; if $t0 > 200, increment $v0",
                        BasicInstructionFormat.I_FORMAT,

                        "100111 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int imm   = operands[2] << 16 >> 16;
                                int rdVal = rsVal + imm;
                                RegisterFile.updateRegister(operands[0], rdVal);

                                if (rdVal > 200) {
                                    int v0 = RegisterFile.getValue(2);
                                    RegisterFile.updateRegister(2,v0 + 1);
                                }
                            }
                        }
                )
        );
        //10. Cool down
        instructionList.add(
                new BasicInstruction("cool $t0,$t1,100",
                        "Cooldown: $t0 = $t1 - imm",
                        BasicInstructionFormat.I_FORMAT,
                        "101000 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] ops = statement.getOperands();
                                int rs = RegisterFile.getValue(ops[1]);
                                int imm   = ops[2] << 16 >> 16;
                                int result = rs - imm;
                                RegisterFile.updateRegister(ops[0], result);
                                }
                            }
                )
        );
    }
}
