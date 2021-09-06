package pl.futurecollars.invoicing.utils

import org.apache.commons.io.FileUtils
import spock.lang.Specification

class FileServiceTest extends Specification {
    String test1 = "fileTest1.txt"
    String test2 = "fileTest2.txt"
    File file1 = new File(test1)
    File file2 = new File(test2)
    FileService fileService;
    String input = "input"

    def setup() {
        fileService = new FileService(test1)
        FileUtils.write(file1, "", "UTF-8", false)
        FileUtils.write(file2, "", "UTF-8", false)
    }

    def "should save string to the file"() {
        given:
        FileUtils.write(new File(test2), input + "\n", "UTF-8")

        when:
        fileService.write(input)

        then:
        FileUtils.contentEquals(new File(test1), new File(test2))
    }

    def "should delete contents of file"() {
        given:
        FileUtils.write(file1, input, "UTF-8")

        when:
        fileService.eraseFile()

        then:
        FileUtils.contentEquals(new File(test1), new File(test2))
    }

    def "should read strings from file into list"() {
        given:
        FileUtils.write(file1, input + "\n", "UTF-8")

        when:
        List<String> list = fileService.read()

        then:
        list == [input]
    }

    def "should return empty list is file is empty"() {
        when:
        def returnedList = fileService.read()

        then:
        returnedList.isEmpty()
    }
}
