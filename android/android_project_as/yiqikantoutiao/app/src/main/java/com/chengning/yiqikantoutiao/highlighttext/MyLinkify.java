package com.chengning.yiqikantoutiao.highlighttext;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.chengning.yiqikantoutiao.highlighttext.MyLinkMovementMethod_.TextClickedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyLinkify {

	private static final String TAG = MyLinkify.class.getSimpleName();

	/**
	 * Bit field indicating that web URLs should be matched in methods that take
	 * an options mask
	 */
	public static final int WEB_URLS = 0x01;

	/**
	 * Bit field indicating that email addresses should be matched in methods
	 * that take an options mask
	 */
	public static final int EMAIL_ADDRESSES = 0x02;

	/**
	 * Bit field indicating that phone numbers should be matched in methods that
	 * take an options mask
	 */
	public static final int PHONE_NUMBERS = 0x04;

	/**
	 * Bit field indicating that street addresses should be matched in methods
	 * that take an options mask
	 */
	public static final int MAP_ADDRESSES = 0x08;

	/**
	 * Bit mask indicating that all available patterns should be matched in
	 * methods that take an options mask
	 */
	public static final int ALL = WEB_URLS | EMAIL_ADDRESSES | PHONE_NUMBERS | MAP_ADDRESSES;

	/**
	 * Don't treat anything with fewer than this many digits as a phone number.
	 */
	private static final int PHONE_NUMBER_MINIMUM_DIGITS = 5;

	/**
	 * Filters out web URL matches that occur after an at-sign (@). This is to
	 * prevent turning the domain name in an email address into a web link.
	 */
	public static final MatchFilter sUrlMatchFilter = new MatchFilter() {
		public final boolean acceptMatch(CharSequence s, int start, int end) {
			if (start == 0) {
				return true;
			}

			if (s.charAt(start - 1) == '@') {
				return false;
			}

			return true;
		}
	};

	/**
	 * Filters out URL matches that don't have enough digits to be a phone
	 * number.
	 */
	public static final MatchFilter sPhoneNumberMatchFilter = new MatchFilter() {
		public final boolean acceptMatch(CharSequence s, int start, int end) {
			int digitCount = 0;

			for (int i = start; i < end; i++) {
				if (Character.isDigit(s.charAt(i))) {
					digitCount++;
					if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
						return true;
					}
				}
			}
			return false;
		}
	};

	/**
	 * Transforms matched phone number text into something suitable to be used
	 * in a tel: URL. It does this by removing everything but the digits and
	 * plus signs. For instance: &apos;+1 (919) 555-1212&apos; becomes
	 * &apos;+19195551212&apos;
	 */
	public static final TransformFilter sPhoneNumberTransformFilter = new TransformFilter() {
		public final String transformUrl(final Matcher match, String url) {
			return Patterns.digitsAndPlusOnly(match);
		}
	};

	/**
	 * MatchFilter enables client code to have more control over what is allowed
	 * to match and become a link, and what is not.
	 * <p/>
	 * For example: when matching web urls you would like things like
	 * http://www.example.com to match, as well as just example.com itelf.
	 * However, you would not want to match against the domain in
	 * support@example.com. So, when matching against a web url pattern you
	 * might also include a MatchFilter that disallows the match if it is
	 * immediately preceded by an at-sign (@).
	 */
	public interface MatchFilter {
		/**
		 * Examines the character span matched by the pattern and determines if
		 * the match should be turned into an actionable link.
		 * 
		 * @param s
		 *            The body of text against which the pattern was matched
		 * @param start
		 *            The index of the first character in s that was matched by
		 *            the pattern - inclusive
		 * @param end
		 *            The index of the last character in s that was matched -
		 *            exclusive
		 * @return Whether this match should be turned into a link
		 */
		boolean acceptMatch(CharSequence s, int start, int end);
	}

	/**
	 * TransformFilter enables client code to have more control over how matched
	 * patterns are represented as URLs.
	 * <p/>
	 * For example: when converting a phone number such as (919) 555-1212 into a
	 * tel: URL the parentheses, white space, and hyphen need to be removed to
	 * produce tel:9195551212.
	 */
	public interface TransformFilter {
		/**
		 * Examines the matched text and either passes it through or uses the
		 * data in the Matcher state to produce a replacement.
		 * 
		 * @param match
		 *            The regex matcher state that found this URL text
		 * @param url
		 *            The text that was matched
		 * @return The transformed form of the URL
		 */
		String transformUrl(final Matcher match, String url);
	}

	/**
	 * Scans the text of the provided Spannable and turns all occurrences of the
	 * link types indicated in the mask into clickable links. If the mask is
	 * nonzero, it also removes any existing URLSpans attached to the Spannable,
	 * to avoid problems if you call it repeatedly on the same text.
	 */
	public static final boolean addLinks(Spannable text, int mask) {
		if (mask == 0) {
			return false;
		}

		URLSpan[] old = text.getSpans(0, text.length(), URLSpan.class);

		for (int i = old.length - 1; i >= 0; i--) {
			text.removeSpan(old[i]);
		}

		ArrayList<LinkSpec> links = new ArrayList<LinkSpec>();

		if ((mask & WEB_URLS) != 0) {
			gatherLinks(links, text, MyPatterns.WEB_URL, new String[] { "http://", "https://", "rtsp://" }, sUrlMatchFilter, null);
		}

		if ((mask & EMAIL_ADDRESSES) != 0) {
			gatherLinks(links, text, Patterns.EMAIL_ADDRESS, new String[] { "mailto:" }, null, null);
		}

		if ((mask & PHONE_NUMBERS) != 0) {
			gatherLinks(links, text, Patterns.PHONE, new String[] { "tel:" }, sPhoneNumberMatchFilter, sPhoneNumberTransformFilter);
		}

		if ((mask & MAP_ADDRESSES) != 0) {
			gatherMapLinks(links, text);
		}

		pruneOverlaps(links);

		if (links.size() == 0) {
			return false;
		}

		for (LinkSpec link : links) {
			applyLink(link.url, link.start, link.end, text);
		}

		return true;
	}

	/**
	 * Scans the text of the provided TextView and turns all occurrences of the
	 * link types indicated in the mask into clickable links. If matches are
	 * found the movement method for the TextView is set to
	 * MyLinkMovementMethod.
	 */
	public static final boolean addLinks(TextView text, int mask) {
		if (mask == 0) {
			return false;
		}

		CharSequence t = text.getText();

		if (t instanceof Spannable) {
			if (addLinks((Spannable) t, mask)) {
				addMyLinkMovementMethod(text);
				return true;
			}

			return false;
		} else {
			SpannableString s = SpannableString.valueOf(t);

			if (addLinks(s, mask)) {
				addMyLinkMovementMethod(text);
				text.setText(s);

				return true;
			}

			return false;
		}
	}

	public static final boolean addJUstHighLightLinks(TextView text, int mask) {
		if (mask == 0) {
			return false;
		}

		CharSequence t = text.getText();

		if (t instanceof Spannable) {
			if (addLinks((Spannable) t, mask)) {
				// addMyLinkMovementMethod(text);
				return true;
			}

			return false;
		} else {
			SpannableString s = SpannableString.valueOf(t);

			if (addLinks(s, mask)) {
				// addMyLinkMovementMethod(text);
				text.setText(s);

				return true;
			}

			return false;
		}
	}

	public static final SpannableString addJUstHighLightLinks(CharSequence text, int mask) {
		if (mask == 0) {
			return SpannableString.valueOf(text);
		}

		CharSequence t = text;

		if (t instanceof Spannable) {
			if (addLinks((Spannable) t, mask)) {
				// addMyLinkMovementMethod(text);
				return SpannableString.valueOf(t);
			}

		} else {
			SpannableString s = SpannableString.valueOf(t);

			if (addLinks(s, mask)) {
				// addMyLinkMovementMethod(text);
				return s;

			}

		}

		return SpannableString.valueOf(text);
	}

	private static final void addMyLinkMovementMethod(final TextView t) {
		MovementMethod m = t.getMovementMethod();

//		if ((m == null) || !(m instanceof MyLinkMovementMethod)) {
//			if (t.getLinksClickable()) {
//				t.setMovementMethod(MyLinkMovementMethod.getInstance());
//			}
//		}
		if ((m == null) || !(m instanceof MyLinkMovementMethod_)) {
			if (t.getLinksClickable()) {
				MyLinkMovementMethod_ myLink = (MyLinkMovementMethod_) MyLinkMovementMethod_.getInstance();
				t.setMovementMethod(myLink);
				myLink.setOnTextClickListener(new TextClickedListener() {

					@Override
					public void onTextClicked(View v, MotionEvent event) {
					}
				});
			}
		}
	}

	/**
	 * Applies a regex to the text of a TextView turning the matches into links.
	 * If links are found then UrlSpans are applied to the link text match
	 * areas, and the movement method for the text is changed to
	 * MyLinkMovementMethod.
	 * 
	 * @param text
	 *            TextView whose text is to be marked-up with links
	 * @param pattern
	 *            Regex pattern to be used for finding links
	 * @param scheme
	 *            Url scheme string (eg <code>http://</code> to be prepended to
	 *            the url of links that do not have a scheme specified in the
	 *            link text
	 */
	public static final void addLinks(TextView text, Pattern pattern, String scheme) {
		addLinks(text, pattern, scheme, null, null);
	}

	/**
	 * Applies a regex to the text of a TextView turning the matches into links.
	 * If links are found then UrlSpans are applied to the link text match
	 * areas, and the movement method for the text is changed to
	 * MyLinkMovementMethod.
	 * 
	 * @param text
	 *            TextView whose text is to be marked-up with links
	 * @param p
	 *            Regex pattern to be used for finding links
	 * @param scheme
	 *            Url scheme string (eg <code>http://</code> to be prepended to
	 *            the url of links that do not have a scheme specified in the
	 *            link text
	 * @param matchFilter
	 *            The filter that is used to allow the client code additional
	 *            control over which pattern matches are to be converted into
	 *            links.
	 */
	public static final void addLinks(TextView text, Pattern p, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
		SpannableString s = SpannableString.valueOf(text.getText());

		if (addLinks(s, p, scheme, matchFilter, transformFilter)) {
			text.setText(s);
			addMyLinkMovementMethod(text);
		}
	}

	public static final void addJustHighLightLinks(TextView text, Pattern p, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
		SpannableString s = SpannableString.valueOf(text.getText());

		if (addLinks(s, p, scheme, matchFilter, transformFilter)) {
			text.setText(s);
		}
	}

	public static final SpannableString getJustHighLightLinks(CharSequence s, Pattern p, String scheme, MatchFilter matchFilter,
                                                              TransformFilter transformFilter) {
		SpannableString value = SpannableString.valueOf(s);

		if (addLinks(value, p, scheme, matchFilter, transformFilter)) {
			return value;
		}

		return new SpannableString(s);
	}

	/**
	 * Applies a regex to a Spannable turning the matches into links.
	 * 
	 * @param text
	 *            Spannable whose text is to be marked-up with links
	 * @param pattern
	 *            Regex pattern to be used for finding links
	 * @param scheme
	 *            Url scheme string (eg <code>http://</code> to be prepended to
	 *            the url of links that do not have a scheme specified in the
	 *            link text
	 */
	public static final boolean addLinks(Spannable text, Pattern pattern, String scheme) {
		return addLinks(text, pattern, scheme, null, null);
	}

	/**
	 * Applies a regex to a Spannable turning the matches into links.
	 * 
	 * @param s
	 *            Spannable whose text is to be marked-up with links
	 * @param p
	 *            Regex pattern to be used for finding links
	 * @param scheme
	 *            Url scheme string (eg <code>http://</code> to be prepended to
	 *            the url of links that do not have a scheme specified in the
	 *            link text
	 * @param matchFilter
	 *            The filter that is used to allow the client code additional
	 *            control over which pattern matches are to be converted into
	 *            links.
	 */
	public static final boolean addLinks(Spannable s, Pattern p, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
		boolean hasMatches = false;
		String prefix = (scheme == null) ? "" : scheme.toLowerCase();
		Matcher m = p.matcher(s);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			boolean allowed = true;

			if (matchFilter != null) {
				allowed = matchFilter.acceptMatch(s, start, end);
			}

			if (allowed) {
				String url = makeUrl(m.group(0), new String[] { prefix }, m, transformFilter);

				Log.v(TAG, String.format("apply link --->%s", url));

				applyLink(url, start, end, s);
				hasMatches = true;
			}
		}

		return hasMatches;
	}

	private static final void applyLink(String url, int start, int end, Spannable text) {
		MyURLSpan span = new MyURLSpan(url);
		text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	}

	private static final String makeUrl(String url, String[] prefixes, Matcher m, TransformFilter filter) {
		if (filter != null) {
			url = filter.transformUrl(m, url);
		}

		boolean hasPrefix = false;

		for (int i = 0; i < prefixes.length; i++) {
			if (url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
				hasPrefix = true;

				// Fix capitalization if necessary
				if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
					url = prefixes[i] + url.substring(prefixes[i].length());
				}

				break;
			}
		}

		if (!hasPrefix) {
			url = prefixes[0] + url;
		}

		return url;
	}

	private static final void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, MatchFilter matchFilter,
                                          TransformFilter transformFilter) {
		Matcher m = pattern.matcher(s);

		while (m.find()) {
			int start = m.start();
			int end = m.end();

			if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
				LinkSpec spec = new LinkSpec();
				String url = makeUrl(m.group(0), schemes, m, transformFilter);

				spec.url = url;
				spec.start = start;
				spec.end = end;

				links.add(spec);
			}
		}
	}

	private static final void gatherMapLinks(ArrayList<LinkSpec> links, Spannable s) {
		String string = s.toString();
		String address;
		int base = 0;

		while ((address = WebView.findAddress(string)) != null) {
			int start = string.indexOf(address);

			if (start < 0) {
				break;
			}

			LinkSpec spec = new LinkSpec();
			int length = address.length();
			int end = start + length;

			spec.start = base + start;
			spec.end = base + end;
			string = string.substring(end);
			base += end;

			String encodedAddress = null;

			try {
				encodedAddress = URLEncoder.encode(address, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				continue;
			}

			spec.url = "geo:0,0?q=" + encodedAddress;
			links.add(spec);
		}
	}

	private static final void pruneOverlaps(ArrayList<LinkSpec> links) {
		Comparator<LinkSpec> c = new Comparator<LinkSpec>() {
			public final int compare(LinkSpec a, LinkSpec b) {
				if (a.start < b.start) {
					return -1;
				}

				if (a.start > b.start) {
					return 1;
				}

				if (a.end < b.end) {
					return 1;
				}

				if (a.end > b.end) {
					return -1;
				}

				return 0;
			}

			public final boolean equals(Object o) {
				return false;
			}
		};

		Collections.sort(links, c);

		int len = links.size();
		int i = 0;

		while (i < len - 1) {
			LinkSpec a = links.get(i);
			LinkSpec b = links.get(i + 1);
			int remove = -1;

			if ((a.start <= b.start) && (a.end > b.start)) {
				if (b.end <= a.end) {
					remove = i + 1;
				} else if ((a.end - a.start) > (b.end - b.start)) {
					remove = i + 1;
				} else if ((a.end - a.start) < (b.end - b.start)) {
					remove = i;
				}

				if (remove != -1) {
					links.remove(remove);
					len--;
					continue;
				}

			}

			i++;
		}
	}

}

class LinkSpec {
	String url;
	int start;
	int end;
}
