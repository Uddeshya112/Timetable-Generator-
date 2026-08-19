package com.example.domain

import com.example.data.model.Course
import com.example.data.model.CourseCategory
import com.example.data.model.TeacherQualification

data class AcademicUploadData(
    val semesterNumber: Int = 5,
    val batches: List<String> = (1..12).map { "B$it" },
    val subgroups: List<String> = (1..4).map { "G$it" },
    val batchCapacity: Int = 120,
    val subgroupCapacity: Int = 30,
    val teachers: List<TeacherQualification> = emptyList(),
    val courses: List<Course> = emptyList(),
    val lectureHalls: List<String> = (1..12).map { "LH-${100 + it}" },
    val labs: List<String> = listOf(
        "AI Lab 1", "AI Lab 2", "Data Struct Lab 1", "Data Struct Lab 2",
        "Systems Lab 1", "Systems Lab 2", "Web Tech Lab 1", "Web Tech Lab 2",
        "Networks Lab 1", "Networks Lab 2", "Hardware Lab 1", "Hardware Lab 2"
    )
)

object AcademicDataParser {

    /**
     * Standard 12 Batches (B1..B12, 120 students each)
     */
    val standard12Batches = (1..12).map { "B$it" }

    /**
     * Standard 4 Subgroups (G1..G4, 30 students each)
     */
    val standard4Subgroups = listOf("G1", "G2", "G3", "G4")

    /**
     * Generates a realistic university curriculum with 6 teachers assigned per subject.
     */
    fun getFullCurriculumForSemester(semester: Int): Pair<List<Course>, List<TeacherQualification>> {
        return when (semester) {
            1, 2 -> {
                // 1st Year (Sem 1 & 2)
                val c1 = Course(1, "BAS101", "Engineering Mathematics I", "Maths-I", semester, CourseCategory.BSC, 3, 1, 0, 4.0, primaryInstructorName = "Dr. K. Ramanujan", preferredRoom = "LH-101")
                val c2 = Course(2, "BAS102", "Engineering Physics", "Physics", semester, CourseCategory.BSC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. C. V. Raman", preferredRoom = "LH-102")
                val c3 = Course(3, "UCS101", "Programming for Problem Solving", "PPS-C", semester, CourseCategory.ESC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Dennis Ritchie", preferredRoom = "LH-103")
                val c4 = Course(4, "UEE101", "Basic Electrical Engineering", "BEE", semester, CourseCategory.ESC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Nikola Tesla", preferredRoom = "LH-104")
                val c5 = Course(5, "UME101", "Engineering Graphics & CAD", "EG-CAD", semester, CourseCategory.ESC, 2, 0, 2, 3.0, primaryInstructorName = "Prof. Leonardo DaVinci", preferredRoom = "LH-105")

                val courses = listOf(c1, c2, c3, c4, c5)
                val teachers = listOf(
                    // 6 Teachers for Maths
                    TeacherQualification("T101", "Dr. K. Ramanujan", "Maths", "BAS101", true, true, false, 18),
                    TeacherQualification("T102", "Dr. S. Radhakrishnan", "Maths", "BAS101", true, true, false, 18),
                    TeacherQualification("T103", "Prof. Meera Sen", "Maths", "BAS101", true, true, false, 18),
                    TeacherQualification("T104", "Dr. Alok Verma", "Maths", "BAS101", true, true, false, 18),
                    TeacherQualification("T105", "Prof. R. N. Bose", "Maths", "BAS101", true, true, false, 18),
                    TeacherQualification("T106", "Dr. Sunita Deshmukh", "Maths", "BAS101", true, true, false, 18),

                    // 6 Teachers for Physics
                    TeacherQualification("T107", "Dr. C. V. Raman", "Physics", "BAS102", true, true, true, 18),
                    TeacherQualification("T108", "Dr. Homi Bhabha", "Physics", "BAS102", true, true, true, 18),
                    TeacherQualification("T109", "Prof. Satyendra Bose", "Physics", "BAS102", true, true, true, 18),
                    TeacherQualification("T110", "Dr. Vikram Sarabhai", "Physics", "BAS102", true, true, true, 18),
                    TeacherQualification("T111", "Prof. A. P. J. Kalam", "Physics", "BAS102", true, true, true, 18),
                    TeacherQualification("T112", "Dr. Meghnad Saha", "Physics", "BAS102", true, true, true, 18),

                    // 6 Teachers for Programming (PPS-C)
                    TeacherQualification("T113", "Prof. Dennis Ritchie", "CSE", "UCS101", true, true, true, 18),
                    TeacherQualification("T114", "Dr. Brian Kernighan", "CSE", "UCS101", true, true, true, 18),
                    TeacherQualification("T115", "Prof. Ken Thompson", "CSE", "UCS101", true, true, true, 18),
                    TeacherQualification("T116", "Dr. Bjarne Stroustrup", "CSE", "UCS101", true, true, true, 18),
                    TeacherQualification("T117", "Prof. James Gosling", "CSE", "UCS101", true, true, true, 18),
                    TeacherQualification("T118", "Dr. Guido van Rossum", "CSE", "UCS101", true, true, true, 18),

                    // 6 Teachers for Basic Electrical (BEE)
                    TeacherQualification("T119", "Dr. Nikola Tesla", "EE", "UEE101", true, true, true, 18),
                    TeacherQualification("T120", "Prof. Thomas Edison", "EE", "UEE101", true, true, true, 18),
                    TeacherQualification("T121", "Dr. Michael Faraday", "EE", "UEE101", true, true, true, 18),
                    TeacherQualification("T122", "Prof. James Maxwell", "EE", "UEE101", true, true, true, 18),
                    TeacherQualification("T123", "Dr. Heinrich Hertz", "EE", "UEE101", true, true, true, 18),
                    TeacherQualification("T124", "Prof. Andre Ampere", "EE", "UEE101", true, true, true, 18),

                    // 6 Teachers for Engineering Graphics
                    TeacherQualification("T125", "Prof. Leonardo DaVinci", "ME", "UME101", true, true, true, 18),
                    TeacherQualification("T126", "Dr. Archimedes", "ME", "UME101", true, true, true, 18),
                    TeacherQualification("T127", "Prof. Euclid", "ME", "UME101", true, true, true, 18),
                    TeacherQualification("T128", "Dr. Pythagoras", "ME", "UME101", true, true, true, 18),
                    TeacherQualification("T129", "Prof. Rene Descartes", "ME", "UME101", true, true, true, 18),
                    TeacherQualification("T130", "Dr. Blaise Pascal", "ME", "UME101", true, true, true, 18)
                )
                Pair(courses, teachers)
            }
            3, 4 -> {
                // 2nd Year (Sem 3 & 4)
                val c1 = Course(1, "UCS301", "Data Structures & Algorithms", "DSA", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Donald Knuth", preferredRoom = "LH-101")
                val c2 = Course(2, "UCS302", "Object Oriented Programming", "OOP-Java", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. James Gosling", preferredRoom = "LH-102")
                val c3 = Course(3, "UCS303", "Digital Logic & Computer Design", "DLD", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Claude Shannon", preferredRoom = "LH-103")
                val c4 = Course(4, "UCS304", "Discrete Mathematics", "Discrete", semester, CourseCategory.BSC, 3, 1, 0, 4.0, primaryInstructorName = "Prof. George Boole", preferredRoom = "LH-104")
                val c5 = Course(5, "UCS305", "Computer Organization & Architecture", "COA", semester, CourseCategory.PCC, 3, 0, 0, 3.0, primaryInstructorName = "Dr. John von Neumann", preferredRoom = "LH-105")

                val courses = listOf(c1, c2, c3, c4, c5)
                val teachers = listOf(
                    // 6 Teachers for DSA
                    TeacherQualification("T201", "Dr. Donald Knuth", "CSE", "UCS301", true, true, true, 18),
                    TeacherQualification("T202", "Prof. Robert Tarjan", "CSE", "UCS301", true, true, true, 18),
                    TeacherQualification("T203", "Dr. Edsger Dijkstra", "CSE", "UCS301", true, true, true, 18),
                    TeacherQualification("T204", "Prof. John Hopcroft", "CSE", "UCS301", true, true, true, 18),
                    TeacherQualification("T205", "Dr. Jeffrey Ullman", "CSE", "UCS301", true, true, true, 18),
                    TeacherQualification("T206", "Prof. Thomas Cormen", "CSE", "UCS301", true, true, true, 18),

                    // 6 Teachers for OOP
                    TeacherQualification("T207", "Prof. James Gosling", "CSE", "UCS302", true, true, true, 18),
                    TeacherQualification("T208", "Dr. Bjarne Stroustrup", "CSE", "UCS302", true, true, true, 18),
                    TeacherQualification("T209", "Prof. Anders Hejlsberg", "CSE", "UCS302", true, true, true, 18),
                    TeacherQualification("T210", "Dr. Alan Kay", "CSE", "UCS302", true, true, true, 18),
                    TeacherQualification("T211", "Prof. Bertrand Meyer", "CSE", "UCS302", true, true, true, 18),
                    TeacherQualification("T212", "Dr. Barbara Liskov", "CSE", "UCS302", true, true, true, 18),

                    // 6 Teachers for DLD
                    TeacherQualification("T213", "Dr. Claude Shannon", "CSE", "UCS303", true, true, true, 18),
                    TeacherQualification("T214", "Prof. Seymour Cray", "CSE", "UCS303", true, true, true, 18),
                    TeacherQualification("T215", "Dr. Gordon Moore", "CSE", "UCS303", true, true, true, 18),
                    TeacherQualification("T216", "Prof. Robert Noyce", "CSE", "UCS303", true, true, true, 18),
                    TeacherQualification("T217", "Dr. Jack Kilby", "CSE", "UCS303", true, true, true, 18),
                    TeacherQualification("T218", "Prof. Federico Faggin", "CSE", "UCS303", true, true, true, 18),

                    // 6 Teachers for Discrete
                    TeacherQualification("T219", "Prof. George Boole", "Maths", "UCS304", true, true, false, 18),
                    TeacherQualification("T220", "Dr. Alan Turing", "Maths", "UCS304", true, true, false, 18),
                    TeacherQualification("T221", "Prof. Alonzo Church", "Maths", "UCS304", true, true, false, 18),
                    TeacherQualification("T222", "Dr. Kurt Godel", "Maths", "UCS304", true, true, false, 18),
                    TeacherQualification("T223", "Prof. David Hilbert", "Maths", "UCS304", true, true, false, 18),
                    TeacherQualification("T224", "Dr. Bertrand Russell", "Maths", "UCS304", true, true, false, 18),

                    // 6 Teachers for COA
                    TeacherQualification("T225", "Dr. John von Neumann", "CSE", "UCS305", true, true, false, 18),
                    TeacherQualification("T226", "Prof. David Patterson", "CSE", "UCS305", true, true, false, 18),
                    TeacherQualification("T227", "Dr. John Hennessy", "CSE", "UCS305", true, true, false, 18),
                    TeacherQualification("T228", "Prof. Gene Amdahl", "CSE", "UCS305", true, true, false, 18),
                    TeacherQualification("T229", "Dr. Maurice Wilkes", "CSE", "UCS305", true, true, false, 18),
                    TeacherQualification("T230", "Prof. Clive Sinclair", "CSE", "UCS305", true, true, false, 18)
                )
                Pair(courses, teachers)
            }
            5, 6 -> {
                // 3rd Year (Sem 5 & 6)
                val c1 = Course(1, "UML501", "Machine Learning", "ML", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Andrew Ng", preferredRoom = "LH-101")
                val c2 = Course(2, "UCS502", "Database Management Systems", "DBMS", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. E. F. Codd", preferredRoom = "LH-102")
                val c3 = Course(3, "UCS503", "Operating Systems & Linux", "OS", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Linus Torvalds", preferredRoom = "LH-103")
                val c4 = Course(4, "UCS504", "Computer Networks & Protocols", "Networks", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Vint Cerf", preferredRoom = "LH-104")
                val c5 = Course(5, "UCS505", "Software Engineering & Agile", "Soft Engg", semester, CourseCategory.PCC, 3, 0, 0, 3.0, primaryInstructorName = "Prof. Fred Brooks", preferredRoom = "LH-105")

                val courses = listOf(c1, c2, c3, c4, c5)
                val teachers = listOf(
                    // 6 Teachers for ML
                    TeacherQualification("T301", "Dr. Andrew Ng", "AI", "UML501", true, true, true, 18),
                    TeacherQualification("T302", "Dr. Fei-Fei Li", "AI", "UML501", true, true, true, 18),
                    TeacherQualification("T303", "Prof. Yann LeCun", "AI", "UML501", true, true, true, 18),
                    TeacherQualification("T304", "Dr. Demis Hassabis", "AI", "UML501", true, true, true, 18),
                    TeacherQualification("T305", "Prof. Geoffrey Hinton", "AI", "UML501", true, true, true, 18),
                    TeacherQualification("T306", "Dr. Yoshua Bengio", "AI", "UML501", true, true, true, 18),

                    // 6 Teachers for DBMS
                    TeacherQualification("T307", "Dr. E. F. Codd", "CSE", "UCS502", true, true, true, 18),
                    TeacherQualification("T308", "Prof. Michael Stonebraker", "CSE", "UCS502", true, true, true, 18),
                    TeacherQualification("T309", "Dr. Jim Gray", "CSE", "UCS502", true, true, true, 18),
                    TeacherQualification("T310", "Prof. Raghu Ramakrishnan", "CSE", "UCS502", true, true, true, 18),
                    TeacherQualification("T311", "Dr. Hector Garcia-Molina", "CSE", "UCS502", true, true, true, 18),
                    TeacherQualification("T312", "Prof. Jeffrey Ullman", "CSE", "UCS502", true, true, true, 18),

                    // 6 Teachers for OS
                    TeacherQualification("T313", "Prof. Linus Torvalds", "CSE", "UCS503", true, true, true, 18),
                    TeacherQualification("T314", "Dr. Andrew Tanenbaum", "CSE", "UCS503", true, true, true, 18),
                    TeacherQualification("T315", "Prof. Dave Cutler", "CSE", "UCS503", true, true, true, 18),
                    TeacherQualification("T316", "Dr. Peter Denning", "CSE", "UCS503", true, true, true, 18),
                    TeacherQualification("T317", "Prof. Butler Lampson", "CSE", "UCS503", true, true, true, 18),
                    TeacherQualification("T318", "Dr. Ken Thompson", "CSE", "UCS503", true, true, true, 18),

                    // 6 Teachers for Networks
                    TeacherQualification("T319", "Dr. Vint Cerf", "CSE", "UCS504", true, true, true, 18),
                    TeacherQualification("T320", "Prof. Bob Kahn", "CSE", "UCS504", true, true, true, 18),
                    TeacherQualification("T321", "Dr. Radia Perlman", "CSE", "UCS504", true, true, true, 18),
                    TeacherQualification("T322", "Prof. Tim Berners-Lee", "CSE", "UCS504", true, true, true, 18),
                    TeacherQualification("T323", "Dr. Van Jacobson", "CSE", "UCS504", true, true, true, 18),
                    TeacherQualification("T324", "Prof. Leonard Kleinrock", "CSE", "UCS504", true, true, true, 18),

                    // 6 Teachers for Software Engg
                    TeacherQualification("T325", "Prof. Fred Brooks", "CSE", "UCS505", true, true, false, 18),
                    TeacherQualification("T326", "Dr. Kent Beck", "CSE", "UCS505", true, true, false, 18),
                    TeacherQualification("T327", "Prof. Martin Fowler", "CSE", "UCS505", true, true, false, 18),
                    TeacherQualification("T328", "Dr. Robert C. Martin", "CSE", "UCS505", true, true, false, 18),
                    TeacherQualification("T329", "Prof. Grady Booch", "CSE", "UCS505", true, true, false, 18),
                    TeacherQualification("T330", "Dr. Barry Boehm", "CSE", "UCS505", true, true, false, 18)
                )
                Pair(courses, teachers)
            }
            else -> {
                // 4th Year (Sem 7 & 8)
                val c1 = Course(1, "UCS701", "Artificial Intelligence & Robotics", "AI-Robotics", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Stuart Russell", preferredRoom = "LH-101")
                val c2 = Course(2, "UCS702", "Cloud Computing & DevOps", "Cloud", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Werner Vogels", preferredRoom = "LH-102")
                val c3 = Course(3, "UCS703", "Information & Cyber Security", "CyberSec", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Bruce Schneier", preferredRoom = "LH-103")
                val c4 = Course(4, "UCS704", "Compiler Design & Optimization", "Compiler", semester, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Alfred Aho", preferredRoom = "LH-104")
                val c5 = Course(5, "UCS705", "Big Data Analytics & Spark", "BigData", semester, CourseCategory.PEC, 3, 0, 0, 3.0, primaryInstructorName = "Prof. Matei Zaharia", preferredRoom = "LH-105")

                val courses = listOf(c1, c2, c3, c4, c5)
                val teachers = listOf(
                    // 6 Teachers for AI & Robotics
                    TeacherQualification("T401", "Dr. Stuart Russell", "AI", "UCS701", true, true, true, 18),
                    TeacherQualification("T402", "Prof. Peter Norvig", "AI", "UCS701", true, true, true, 18),
                    TeacherQualification("T403", "Dr. Rodney Brooks", "AI", "UCS701", true, true, true, 18),
                    TeacherQualification("T404", "Prof. Sebastian Thrun", "AI", "UCS701", true, true, true, 18),
                    TeacherQualification("T405", "Dr. Judea Pearl", "AI", "UCS701", true, true, true, 18),
                    TeacherQualification("T406", "Prof. Daphne Koller", "AI", "UCS701", true, true, true, 18),

                    // 6 Teachers for Cloud
                    TeacherQualification("T407", "Dr. Werner Vogels", "CSE", "UCS702", true, true, true, 18),
                    TeacherQualification("T408", "Prof. Kelsey Hightower", "CSE", "UCS702", true, true, true, 18),
                    TeacherQualification("T409", "Dr. Adrian Cockcroft", "CSE", "UCS702", true, true, true, 18),
                    TeacherQualification("T410", "Prof. Brendan Burns", "CSE", "UCS702", true, true, true, 18),
                    TeacherQualification("T411", "Dr. Joe Beda", "CSE", "UCS702", true, true, true, 18),
                    TeacherQualification("T412", "Prof. Craig McLuckie", "CSE", "UCS702", true, true, true, 18),

                    // 6 Teachers for CyberSec
                    TeacherQualification("T413", "Prof. Bruce Schneier", "CSE", "UCS703", true, true, true, 18),
                    TeacherQualification("T414", "Dr. Whitfield Diffie", "CSE", "UCS703", true, true, true, 18),
                    TeacherQualification("T415", "Prof. Martin Hellman", "CSE", "UCS703", true, true, true, 18),
                    TeacherQualification("T416", "Dr. Ron Rivest", "CSE", "UCS703", true, true, true, 18),
                    TeacherQualification("T417", "Prof. Adi Shamir", "CSE", "UCS703", true, true, true, 18),
                    TeacherQualification("T418", "Dr. Leonard Adleman", "CSE", "UCS703", true, true, true, 18),

                    // 6 Teachers for Compiler
                    TeacherQualification("T419", "Dr. Alfred Aho", "CSE", "UCS704", true, true, true, 18),
                    TeacherQualification("T420", "Prof. Jeffrey Ullman", "CSE", "UCS704", true, true, true, 18),
                    TeacherQualification("T421", "Dr. Ravi Sethi", "CSE", "UCS704", true, true, true, 18),
                    TeacherQualification("T422", "Prof. Monica Lam", "CSE", "UCS704", true, true, true, 18),
                    TeacherQualification("T423", "Dr. Chris Lattner", "CSE", "UCS704", true, true, true, 18),
                    TeacherQualification("T424", "Prof. Keith Cooper", "CSE", "UCS704", true, true, true, 18),

                    // 6 Teachers for Big Data
                    TeacherQualification("T425", "Prof. Matei Zaharia", "CSE", "UCS705", true, true, false, 18),
                    TeacherQualification("T426", "Dr. Doug Cutting", "CSE", "UCS705", true, true, false, 18),
                    TeacherQualification("T427", "Prof. Mike Cafarella", "CSE", "UCS705", true, true, false, 18),
                    TeacherQualification("T428", "Dr. Ion Stoica", "CSE", "UCS705", true, true, false, 18),
                    TeacherQualification("T429", "Prof. Scott Shenker", "CSE", "UCS705", true, true, false, 18),
                    TeacherQualification("T430", "Dr. Michael Franklin", "CSE", "UCS705", true, true, false, 18)
                )
                Pair(courses, teachers)
            }
        }
    }

    /**
     * Complete default CSV template preloaded with 8-Semester University standard scheme
     * (12 Batches, 4 Subgroups, 6 Teachers per subject, Common Lectures 120 cap, Labs 30 cap)
     */
    val defaultCseTemplate = """
# UNIVERSITY SCHEME: 8 SEMESTERS • 12 BATCHES (120 CAP) • 4 SUBGROUPS (30 CAP) • 6 TEACHERS/COURSE

# BATCHES
B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12

# SUBGROUPS_PER_BATCH
G1, G2, G3, G4

# COURSES (Code, Name, Category, L, T, P, Credits, PrimaryInstructor, Room)
UML501, Machine Learning, PCC, 3, 0, 2, 4.0, Dr. Andrew Ng, LH-101
UCS502, Database Management Systems, PCC, 3, 0, 2, 4.0, Dr. E. F. Codd, LH-102
UCS503, Operating Systems & Linux, PCC, 3, 0, 2, 4.0, Prof. Linus Torvalds, LH-103
UCS504, Computer Networks & Protocols, PCC, 3, 0, 2, 4.0, Dr. Vint Cerf, LH-104
UCS505, Software Engineering & Agile, PCC, 3, 0, 0, 3.0, Prof. Fred Brooks, LH-105

# TEACHERS (ID, Name, Dept, Subject, MaxHours, Lecture(Y/N), Tutorial(Y/N), Lab(Y/N))
T301, Dr. Andrew Ng, AI, UML501, 18, Y, Y, Y
T302, Dr. Fei-Fei Li, AI, UML501, 18, Y, Y, Y
T303, Prof. Yann LeCun, AI, UML501, 18, Y, Y, Y
T304, Dr. Demis Hassabis, AI, UML501, 18, Y, Y, Y
T305, Prof. Geoffrey Hinton, AI, UML501, 18, Y, Y, Y
T306, Dr. Yoshua Bengio, AI, UML501, 18, Y, Y, Y
T307, Dr. E. F. Codd, CSE, UCS502, 18, Y, Y, Y
T308, Prof. Michael Stonebraker, CSE, UCS502, 18, Y, Y, Y
T309, Dr. Jim Gray, CSE, UCS502, 18, Y, Y, Y
T310, Prof. Raghu Ramakrishnan, CSE, UCS502, 18, Y, Y, Y
T311, Dr. Hector Garcia-Molina, CSE, UCS502, 18, Y, Y, Y
T312, Prof. Jeffrey Ullman, CSE, UCS502, 18, Y, Y, Y
T313, Prof. Linus Torvalds, CSE, UCS503, 18, Y, Y, Y
T314, Dr. Andrew Tanenbaum, CSE, UCS503, 18, Y, Y, Y
T315, Prof. Dave Cutler, CSE, UCS503, 18, Y, Y, Y
T316, Dr. Peter Denning, CSE, UCS503, 18, Y, Y, Y
T317, Prof. Butler Lampson, CSE, UCS503, 18, Y, Y, Y
T318, Dr. Ken Thompson, CSE, UCS503, 18, Y, Y, Y
T319, Dr. Vint Cerf, CSE, UCS504, 18, Y, Y, Y
T320, Prof. Bob Kahn, CSE, UCS504, 18, Y, Y, Y
T321, Dr. Radia Perlman, CSE, UCS504, 18, Y, Y, Y
T322, Prof. Tim Berners-Lee, CSE, UCS504, 18, Y, Y, Y
T323, Dr. Van Jacobson, CSE, UCS504, 18, Y, Y, Y
T324, Prof. Leonard Kleinrock, CSE, UCS504, 18, Y, Y, Y
T325, Prof. Fred Brooks, CSE, UCS505, 18, Y, Y, N
T326, Dr. Kent Beck, CSE, UCS505, 18, Y, Y, N
T327, Prof. Martin Fowler, CSE, UCS505, 18, Y, Y, N
T328, Dr. Robert C. Martin, CSE, UCS505, 18, Y, Y, N
T329, Prof. Grady Booch, CSE, UCS505, 18, Y, Y, N
T330, Dr. Barry Boehm, CSE, UCS505, 18, Y, Y, N

# ROOMS (LectureHalls_120Cap | Labs_30Cap)
LH-101, LH-102, LH-103, LH-104, LH-105, LH-106, LH-107, LH-108, LH-109, LH-110, LH-111, LH-112 | AI Lab 1, AI Lab 2, Data Struct Lab 1, Data Struct Lab 2, Systems Lab 1, Systems Lab 2, Web Tech Lab 1, Web Tech Lab 2, Networks Lab 1, Networks Lab 2, Hardware Lab 1, Hardware Lab 2
    """.trimIndent()

    fun parseCsvText(rawText: String, semester: Int = 5): AcademicUploadData {
        val batches = mutableListOf<String>()
        val subgroups = mutableListOf<String>()
        val teachers = mutableListOf<TeacherQualification>()
        val courses = mutableListOf<Course>()
        val lectureHalls = mutableListOf<String>()
        val labs = mutableListOf<String>()

        var currentSection = ""

        rawText.lines().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEach

            if (line.startsWith("#")) {
                val header = line.uppercase()
                when {
                    "BATCH" in header && "SUBGROUP" !in header -> currentSection = "BATCHES"
                    "SUBGROUP" in header -> currentSection = "SUBGROUPS"
                    "TEACHER" in header || "FACULTY" in header -> currentSection = "TEACHERS"
                    "COURSE" in header || "SUBJECT" in header -> currentSection = "COURSES"
                    "ROOM" in header || "HALL" in header || "LAB" in header -> currentSection = "ROOMS"
                }
                return@forEach
            }

            when (currentSection) {
                "BATCHES" -> {
                    line.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { batches.add(it) }
                }
                "SUBGROUPS" -> {
                    line.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { subgroups.add(it) }
                }
                "TEACHERS" -> {
                    val parts = line.split(",").map { it.trim() }
                    if (parts.size >= 4) {
                        val id = parts[0]
                        val name = parts[1]
                        val dept = parts[2]
                        val subject = parts[3]
                        val maxHours = parts.getOrNull(4)?.toIntOrNull() ?: 18
                        val canL = parts.getOrNull(5)?.equals("Y", ignoreCase = true) ?: true
                        val canT = parts.getOrNull(6)?.equals("Y", ignoreCase = true) ?: true
                        val canP = parts.getOrNull(7)?.equals("Y", ignoreCase = true) ?: true

                        teachers.add(
                            TeacherQualification(
                                teacherId = id,
                                teacherName = name,
                                department = dept,
                                qualifiedCourseCode = subject,
                                canTeachLecture = canL,
                                canTeachTutorial = canT,
                                canTeachLab = canP,
                                maxWeeklyWorkloadHours = maxHours,
                                currentWeeklyWorkloadHours = 0,
                                preferredPeriods = "Morning P1-P4",
                                researchPeriods = "Afternoon"
                            )
                        )
                    }
                }
                "COURSES" -> {
                    val parts = line.split(",").map { it.trim() }
                    if (parts.size >= 4) {
                        val code = parts[0]
                        val name = parts[1]
                        val catStr = parts.getOrNull(2) ?: "PCC"
                        val category = try { CourseCategory.valueOf(catStr) } catch (_: Exception) { CourseCategory.PCC }
                        val l = parts.getOrNull(3)?.toIntOrNull() ?: 3
                        val t = parts.getOrNull(4)?.toIntOrNull() ?: 0
                        val p = parts.getOrNull(5)?.toIntOrNull() ?: 2
                        val cr = parts.getOrNull(6)?.toDoubleOrNull() ?: 4.0
                        val instructor = parts.getOrNull(7) ?: "Faculty"
                        val room = parts.getOrNull(8) ?: "LH-101"

                        courses.add(
                            Course(
                                id = (courses.size + 1).toLong(),
                                code = code,
                                name = name,
                                shortName = code,
                                semesterNumber = semester,
                                category = category,
                                lectureHours = l,
                                tutorialHours = t,
                                practicalHours = p,
                                credits = cr,
                                primaryInstructorName = instructor,
                                preferredRoom = room
                            )
                        )
                    }
                }
                "ROOMS" -> {
                    if ("|" in line) {
                        val splits = line.split("|")
                        splits[0].split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { lectureHalls.add(it) }
                        splits.getOrNull(1)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.forEach { labs.add(it) }
                    } else {
                        line.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { lectureHalls.add(it) }
                    }
                }
            }
        }

        val (defCourses, defTeachers) = getFullCurriculumForSemester(semester)

        val finalBatches = if (batches.isEmpty()) standard12Batches else batches
        val finalSubgroups = if (subgroups.isEmpty()) standard4Subgroups else subgroups
        val finalCourses = if (courses.isEmpty()) defCourses else courses
        val finalTeachers = if (teachers.isEmpty()) defTeachers else teachers
        val finalLectureHalls = if (lectureHalls.isEmpty()) (1..12).map { "LH-${100 + it}" } else lectureHalls
        val finalLabs = if (labs.isEmpty()) listOf(
            "AI Lab 1", "AI Lab 2", "Data Struct Lab 1", "Data Struct Lab 2",
            "Systems Lab 1", "Systems Lab 2", "Web Tech Lab 1", "Web Tech Lab 2",
            "Networks Lab 1", "Networks Lab 2", "Hardware Lab 1", "Hardware Lab 2"
        ) else labs

        return AcademicUploadData(
            semesterNumber = semester,
            batches = finalBatches,
            subgroups = finalSubgroups,
            batchCapacity = 120,
            subgroupCapacity = 30,
            teachers = finalTeachers,
            courses = finalCourses,
            lectureHalls = finalLectureHalls,
            labs = finalLabs
        )
    }

    /**
     * Builds an Excel-compatible CSV export string of any Academic Upload Data
     */
    fun buildCsvSchemeExport(data: AcademicUploadData): String {
        val sb = StringBuilder()
        sb.appendLine("# UNIVERSITY ACADEMIC SCHEME • SEMESTER ${data.semesterNumber}")
        sb.appendLine("# 12 Batches (120 Capacity each) • 4 Subgroups (30 Capacity each) • 6 Teachers per Course")
        sb.appendLine()
        sb.appendLine("# BATCHES")
        sb.appendLine(data.batches.joinToString(", "))
        sb.appendLine()
        sb.appendLine("# SUBGROUPS_PER_BATCH")
        sb.appendLine(data.subgroups.joinToString(", "))
        sb.appendLine()
        sb.appendLine("# COURSES (Code, Name, Category, L, T, P, Credits, PrimaryInstructor, Room)")
        data.courses.forEach { c ->
            sb.appendLine("${c.code}, ${c.name}, ${c.category.name}, ${c.lectureHours}, ${c.tutorialHours}, ${c.practicalHours}, ${c.credits}, ${c.primaryInstructorName}, ${c.preferredRoom}")
        }
        sb.appendLine()
        sb.appendLine("# TEACHERS (ID, Name, Dept, Subject, MaxHours, Lecture(Y/N), Tutorial(Y/N), Lab(Y/N))")
        data.teachers.forEach { t ->
            sb.appendLine("${t.teacherId}, ${t.teacherName}, ${t.department}, ${t.qualifiedCourseCode}, ${t.maxWeeklyWorkloadHours}, ${if (t.canTeachLecture) "Y" else "N"}, ${if (t.canTeachTutorial) "Y" else "N"}, ${if (t.canTeachLab) "Y" else "N"}")
        }
        sb.appendLine()
        sb.appendLine("# ROOMS (LectureHalls | Labs)")
        sb.appendLine("${data.lectureHalls.joinToString(", ")} | ${data.labs.joinToString(", ")}")
        return sb.toString()
    }
}

