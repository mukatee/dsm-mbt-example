#ALL SID UNIQUE
#BLOCKS = 7
#TYPES = 2
#TYPE128368.NAME = DFT1
#TYPE128424.NAME = DFT2
#BLOCK128365.NAME = DFP1
#BLOCK128365.INPORT_COUNT = 15
#BLOCK128365.OUTPORT_COUNT = 22
#BLOCK128365.DESCRIPTION = Description128365
#BLOCK128366.NAME = DFP2
#BLOCK128366.INPORT_COUNT = 15
#BLOCK128366.OUTPORT_COUNT = 22
#BLOCK128366.DESCRIPTION = Description128366
#BLOCK128367.NAME = DFP3
#BLOCK128367.INPORT_COUNT = 15
#BLOCK128367.OUTPORT_COUNT = 22
#BLOCK128367.DESCRIPTION = Description128367
#BLOCK128370.NAME = DFP4
#BLOCK128370.INPORT_COUNT = 15
#BLOCK128370.OUTPORT_COUNT = 22
#!BLOCK128370.DESCRIPTION
#BLOCK128393.NAME = DFP5
#BLOCK128393.INPORT_COUNT = 2
#BLOCK128393.OUTPORT_COUNT = 2
#BLOCK128393.DESCRIPTION = Description128393
#BLOCK128420.NAME = DFP6
#BLOCK128420.INPORT_COUNT = 2
#BLOCK128420.OUTPORT_COUNT = 2
#BLOCK128420.DESCRIPTION = Description128420
#BLOCK128435.NAME = DFP7
#BLOCK128435.INPORT_COUNT = 0
#BLOCK128435.OUTPORT_COUNT = 0
#!BLOCK128435.DESCRIPTION

import glob

def check(value, error):
    if not value:
        print(error)

dfts = glob.glob("DFT*.mdl")
dft_count = len(dfts)
check(dft_count == 2, "Number of DFT output should be 2, was "+str(dft_count))

dfp_inports = {"DFP1":15, "DFP2":15, "DFP3":15, "DFP4":15, "DFP5":2, "DFP6":2, "DFP7":0}
dfp_outports = {"DFP1":22, "DFP2":22, "DFP3":22, "DFP4":22, "DFP5":2, "DFP6":2, "DFP7":0}
dfp_descriptions = {"DFP1":"Description128365", "DFP2":"Description128366", "DFP3":"Description128367", "DFP4":None, "DFP5":"Description128393", "DFP6":"Description128420", "DFP7":None}
lines_expected = ["DFP3.1->DFP2.13", "DFP4.2->DFP5.2"]

with open("Top_Level_DFT.mdl") as f:
    content = f.readlines()

sids = []
dfp_names = []
inports = []
outports = []
name = None
dfp_count = 0
lines_actual = []
line_count = 0

for line in content:
    l = line.strip()
    if l.startswith("BlockType"):
        check(l.endswith("ModelReference"), "BlockType should always be 'ModelReference'. For "+str(name)+" found "+l)
    if l.startswith("Block {"):
        if dfp_count > 0:
            expected = dfp_inports[name];
            actual = len(inports)
            check(actual == expected, "Number of inports for "+name+" expected "+str(expected)+" was "+str(actual))
            expected = dfp_outports[name];
            actual = len(outports)
            check(actual == expected, "Number of outports for "+name+" expected "+str(expected)+" was "+str(actual))
            expected = dfp_descriptions[name]
            check (description == expected, "Description for "+name+" expected "+str(expected)+" was "+str(description))
        description = None
        name = None
        dfp_count += 1
        inports = []
        outports = []
    if l.startswith("Name\t"):
        name = l.split('"')[1]
        dfp_names.append(name)
    if l.startswith("Description\t"):
        description = l.split('"')[1]
    if l.startswith("SID\t"):
        sid = l.split('"')[1]
        check(sid not in sids, "SID should be unique. '"+sid+"' was not.")
        sids.append(sid)
    if l.startswith("List {"):
        port_type = None
    if l.startswith("ListType"):
        if l.endswith("InputPortNames"):
            port_type = "in"
        if l.endswith("OutputPortNames"):
            port_type = "out"
    if l.startswith("port") and port_type == "in":
        inports.append(l.split('"')[1])
    if l.startswith("port") and port_type == "out":
        outports.append(l.split('"')[1])
    if l.startswith("Line {"):
        if (line_count > 0):
            lines_actual.append(str(src_block)+"."+str(src_port)+"->"+str(dst_block)+"."+str(dst_port))
        src_block = None
        dst_block = None
        src_port = None
        dst_port = None
        line_count += 1
    if l.startswith("SrcBlock"):
        src_block = l.split('"')[1]
    if l.startswith("DstBlock"):
        dst_block = l.split('"')[1]
    if l.startswith("SrcPort"):
        src_port = l[8:].strip()
    if l.startswith("DstPort"):
        dst_port = l[8:].strip()
    print(name)
check(dfp_count == 7, "Number of DFP should be 7, was "+str(dfp_count))
check(line_count == 14, "Number of lines expected 14, was "+str(line_count))
for line in lines_expected:
    check(line in lines_actual, "Line not found:"+line)

