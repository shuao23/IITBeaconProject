package ipro239.iitbeaconproject;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuao23 on 3/25/2017.
 */

public class BeaconXMLParser {
    private static final String namespace = null;

    public List<BeaconDisplay> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<BeaconDisplay> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List beacons = new ArrayList();

        parser.require(XmlPullParser.START_TAG, namespace, "beacons");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("beacon")) {
                beacons.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return beacons;
    }

    private BeaconDisplay readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "beacon");
        String instanceID = null;
        String name = null;
        int tag = 0;
        String description = null;
        String url = null;
        Coord location = new Coord(0, 0);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("id")) {
                instanceID = readID(parser);
            } else if (tagName.equals("name")) {
                name = readName(parser);
            } else if (tagName.equals("tags")) {
                tag = readTag(parser);
            } else if (tagName.equals("description")) {
                description = readDescription(parser);
            } else if (tagName.equals("url")) {
                url = readURL(parser);
            } else if (tagName.equals("location")) {
                location = readLoc(parser);
            } else {
                skip(parser);
            }
        }
        BeaconDisplay.Builder builder = new BeaconDisplay.Builder();
        builder.setName(name)
                .setInstanceID(instanceID)
                .setTags(tag)
                .setDescription(description)
                .setUrl(url)
                .setLocation(location);
        return builder.build();
    }

    private String readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "id");
        String idInString = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "id");
        return idInString;
    }

    private int readTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "tags");
        String tagInString = readText(parser);
        int tag = (int)Long.parseLong(tagInString, 16);
        parser.require(XmlPullParser.END_TAG, namespace, "tags");
        return tag;
    }

    private Coord readLoc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "location");
        int x = 0, y = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("x")) {
                parser.require(XmlPullParser.START_TAG, namespace, "x");
                String xInString = readText(parser);
                x = Integer.parseInt(xInString,10);
                parser.require(XmlPullParser.END_TAG, namespace, "x");
            } else if (tagName.equals("y")) {
                parser.require(XmlPullParser.START_TAG, namespace, "y");
                String yInString = readText(parser);
                y = Integer.parseInt(yInString,10);
                parser.require(XmlPullParser.END_TAG, namespace, "y");
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, namespace, "location");
        return new Coord(x, y);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "name");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "name");
        return summary;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "description");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "description");
        return summary;
    }

    private String readURL(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, namespace, "url");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "url");
        return summary;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
