.text
.globl main

main:

    fuel $t0,0          # $t0 = active tire
    fuel $t3,999        # $t3 = spare tire
    fuel $t1,0          # $t1 = safe wear level

HAZARD_LOOP:
    lup  $t0            # tire wear++

    hz   $t0,$t1,PITSTOP   # branch if not equal

    cklap $t0,$t0,HAZARD_LOOP   # unconditional loop

PITSTOP:
    pit $t0,$t3         # swap registers

END:
    cklap $t0,$t0,END   # loop forever
