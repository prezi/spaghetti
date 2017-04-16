package com.prezi.spaghetti.haxe

/**
 * Haxe externs from the js.html package.
 *
 * Partially generated with below script, usage:
 *
 * $ find /usr/lib/haxe/std/js/html -name "*.hx" | perl process.pl | sort
 */
/*
my @undefs = [];
while (<>) {
	my $filename = $_;
	open my $file, $filename or die "Could not open file: $!";
	my $package;
	my $native;
	my $class;
	foreach $line (<$file>) {
		if ($line =~ m/^\s*package\s+([\w0-9_.]+)\s*;\s*$/) {
			# print "Package: $1\n";
			$package = $1;
		} elsif ($line =~ m/^\@:native\("([\w0-9_]+)"\)/) {
			# print "Native: $1\n";
			$native = $1;
		} elsif ($line =~ m/^.*class ([\w0-9_]+)/) {
			# print "Class: $1\n";
			$class = $1;
		}
	}
	if (!$package or !$native or !$class) {
		push @undefs, $filename
	} else {
		print "\t$native: \"$package.$class\",\n";
	}
	close $file;
}

foreach my $undef (@undefs) {
	# print "Problematic $undef"
}
 */
final class HaxeJsHtmlExterns {
	public static final def EXTERNS = [
			// Added manually to correct for stdlib mixup in Haxe 3.1
			// https://github.com/HaxeFoundation/haxe/issues/3644
			HTMLElement: "js.html.Element",

			// Generated from js.html stdlib
			AbstractWorker: "js.html.AbstractWorker",
			AnalyserNode: "js.html.audio.AnalyserNode",
			Animation: "js.html.Animation",
			AnimationEvent: "js.html.AnimationEvent",
			AnimationList: "js.html.AnimationList",
			ArrayBuffer: "js.html.ArrayBuffer",
			ArrayBufferView: "js.html.ArrayBufferView",
			Attr: "js.html.Attr",
			Audio: "js.html.Audio",
			AudioBuffer: "js.html.audio.AudioBuffer",
			AudioBufferSourceNode: "js.html.audio.AudioBufferSourceNode",
			AudioContext: "js.html.audio.AudioContext",
			AudioDestinationNode: "js.html.audio.AudioDestinationNode",
			AudioGain: "js.html.audio.AudioGain",
			AudioListener: "js.html.audio.AudioListener",
			AudioNode: "js.html.audio.AudioNode",
			AudioParam: "js.html.audio.AudioParam",
			AudioProcessingEvent: "js.html.audio.AudioProcessingEvent",
			AudioSourceNode: "js.html.audio.AudioSourceNode",
			BarInfo: "js.html.BarInfo",
			BatteryManager: "js.html.BatteryManager",
			BeforeLoadEvent: "js.html.BeforeLoadEvent",
			BiquadFilterNode: "js.html.audio.BiquadFilterNode",
			Blob: "js.html.Blob",
			CDATASection: "js.html.CDATASection",
			CSSCharsetRule: "js.html.CSSCharsetRule",
			CSSFilterValue: "js.html.CSSFilterValue",
			CSSFontFaceRule: "js.html.CSSFontFaceRule",
			CSSImportRule: "js.html.CSSImportRule",
			CSSKeyframeRule: "js.html.CSSKeyframeRule",
			CSSKeyframesRule: "js.html.CSSKeyframesRule",
			CSSMatrix: "js.html.CSSMatrix",
			CSSMediaRule: "js.html.CSSMediaRule",
			CSSPageRule: "js.html.CSSPageRule",
			CSSPrimitiveValue: "js.html.CSSPrimitiveValue",
			CSSRule: "js.html.CSSRule",
			CSSRuleList: "js.html.CSSRuleList",
			CSSStyleDeclaration: "js.html.CSSStyleDeclaration",
			CSSStyleRule: "js.html.CSSStyleRule",
			CSSStyleSheet: "js.html.CSSStyleSheet",
			CSSTransformValue: "js.html.CSSTransformValue",
			CSSUnknownRule: "js.html.CSSUnknownRule",
			CSSValue: "js.html.CSSValue",
			CSSValueList: "js.html.CSSValueList",
			CanvasGradient: "js.html.CanvasGradient",
			CanvasPattern: "js.html.CanvasPattern",
			CanvasRenderingContext2D: "js.html.CanvasRenderingContext2D",
			CanvasRenderingContext: "js.html.CanvasRenderingContext",
			ChannelMergerNode: "js.html.audio.ChannelMergerNode",
			ChannelSplitterNode: "js.html.audio.ChannelSplitterNode",
			CharacterData: "js.html.CharacterData",
			ClientRect: "js.html.ClientRect",
			ClientRectList: "js.html.ClientRectList",
			Clipboard: "js.html.Clipboard",
			CloseEvent: "js.html.CloseEvent",
			Comment: "js.html.Comment",
			CompositionEvent: "js.html.CompositionEvent",
			Console: "js.html.Console",
			ConvolverNode: "js.html.audio.ConvolverNode",
			Coordinates: "js.html.Coordinates",
			Counter: "js.html.Counter",
			Crypto: "js.html.Crypto",
			CustomEvent: "js.html.CustomEvent",
			DOMApplicationCache: "js.html.DOMApplicationCache",
			DOMError: "js.html.DOMError",
			DOMException: "js.html.DOMCoreException",
			DOMFileSystem: "js.html.fs.FileSystem",
			DOMFileSystemSync: "js.html.fs.FileSystemSync",
			DOMImplementation: "js.html.DOMImplementation",
			DOMParser: "js.html.DOMParser",
			DOMSettableTokenList: "js.html.DOMSettableTokenList",
			DOMStringList: "js.html.DOMStringList",
			DOMStringMap: "js.html.DOMStringMap",
			DOMTokenList: "js.html.DOMTokenList",
			DataTransferItem: "js.html.DataTransferItem",
			DataTransferItemList: "js.html.DataTransferItemList",
			DataView: "js.html.DataView",
			Database: "js.html.sql.Database",
			DatabaseSync: "js.html.sql.DatabaseSync",
			DedicatedWorkerContext: "js.html.DedicatedWorkerContext",
			DelayNode: "js.html.audio.DelayNode",
			DeviceAcceleration: "js.html.DeviceAcceleration",
			DeviceMotionEvent: "js.html.DeviceMotionEvent",
			DeviceOrientationEvent: "js.html.DeviceOrientationEvent",
			DeviceRotationRate: "js.html.DeviceRotationRate",
			DirectoryEntry: "js.html.fs.DirectoryEntry",
			DirectoryEntrySync: "js.html.fs.DirectoryEntrySync",
			DirectoryReader: "js.html.fs.DirectoryReader",
			DirectoryReaderSync: "js.html.fs.DirectoryReaderSync",
			Document: "js.html.Document",
			DocumentFragment: "js.html.DocumentFragment",
			DocumentType: "js.html.DocumentType",
			DynamicsCompressorNode: "js.html.audio.DynamicsCompressorNode",
			EXTTextureFilterAnisotropic: "js.html.webgl.EXTTextureFilterAnisotropic",
			Element: "js.html.Element",
			ElementTimeControl: "js.html.ElementTimeControl",
			Entity: "js.html.Entity",
			EntityReference: "js.html.EntityReference",
			Entry: "js.html.fs.Entry",
			EntryArray: "js.html.fs.EntryArray",
			EntryArraySync: "js.html.fs.EntryArraySync",
			EntrySync: "js.html.fs.EntrySync",
			ErrorEvent: "js.html.ErrorEvent",
			Event: "js.html.Event",
			EventException: "js.html.EventException",
			EventSource: "js.html.EventSource",
			EventTarget: "js.html.EventTarget",
			File: "js.html.File",
			FileEntry: "js.html.fs.FileEntry",
			FileEntrySync: "js.html.fs.FileEntrySync",
			FileError: "js.html.fs.FileError",
			FileException: "js.html.fs.FileException",
			FileList: "js.html.FileList",
			FileReader: "js.html.FileReader",
			FileReaderSync: "js.html.FileReaderSync",
			FileWriter: "js.html.fs.FileWriter",
			FileWriterSync: "js.html.fs.FileWriterSync",
			Float32Array: "js.html.Float32Array",
			Float64Array: "js.html.Float64Array",
			FormData: "js.html.DOMFormData",
			GainNode: "js.html.audio.GainNode",
			Gamepad: "js.html.Gamepad",
			GamepadList: "js.html.GamepadList",
			Geolocation: "js.html.Geolocation",
			Geoposition: "js.html.Geoposition",
			HTMLAllCollection: "js.html.HTMLAllCollection",
			HTMLAnchorElement: "js.html.AnchorElement",
			HTMLAppletElement: "js.html.AppletElement",
			HTMLAreaElement: "js.html.AreaElement",
			HTMLAudioElement: "js.html.AudioElement",
			HTMLBRElement: "js.html.BRElement",
			HTMLBaseElement: "js.html.BaseElement",
			HTMLBaseFontElement: "js.html.BaseFontElement",
			HTMLBodyElement: "js.html.BodyElement",
			HTMLButtonElement: "js.html.ButtonElement",
			HTMLCanvasElement: "js.html.CanvasElement",
			HTMLCollection: "js.html.HTMLCollection",
			HTMLContentElement: "js.html.ContentElement",
			HTMLDListElement: "js.html.DListElement",
			HTMLDataListElement: "js.html.DataListElement",
			HTMLDetailsElement: "js.html.DetailsElement",
			HTMLDirectoryElement: "js.html.DirectoryElement",
			HTMLDivElement: "js.html.DivElement",
			HTMLEmbedElement: "js.html.EmbedElement",
			HTMLFieldSetElement: "js.html.FieldSetElement",
			HTMLFontElement: "js.html.FontElement",
			HTMLFormElement: "js.html.FormElement",
			HTMLFrameElement: "js.html.FrameElement",
			HTMLFrameSetElement: "js.html.FrameSetElement",
			HTMLHRElement: "js.html.HRElement",
			HTMLHeadElement: "js.html.HeadElement",
			HTMLHeadingElement: "js.html.HeadingElement",
			HTMLHtmlElement: "js.html.HtmlElement",
			HTMLIFrameElement: "js.html.IFrameElement",
			HTMLImageElement: "js.html.ImageElement",
			HTMLInputElement: "js.html.InputElement",
			HTMLKeygenElement: "js.html.KeygenElement",
			HTMLLIElement: "js.html.LIElement",
			HTMLLabelElement: "js.html.LabelElement",
			HTMLLegendElement: "js.html.LegendElement",
			HTMLLinkElement: "js.html.LinkElement",
			HTMLMapElement: "js.html.MapElement",
			HTMLMarqueeElement: "js.html.MarqueeElement",
			HTMLMediaElement: "js.html.MediaElement",
			HTMLMenuElement: "js.html.MenuElement",
			HTMLMetaElement: "js.html.MetaElement",
			HTMLMeterElement: "js.html.MeterElement",
			HTMLModElement: "js.html.ModElement",
			HTMLOListElement: "js.html.OListElement",
			HTMLObjectElement: "js.html.ObjectElement",
			HTMLOptGroupElement: "js.html.OptGroupElement",
			HTMLOptionElement: "js.html.OptionElement",
			HTMLOptionsCollection: "js.html.HTMLOptionsCollection",
			HTMLOutputElement: "js.html.OutputElement",
			HTMLParagraphElement: "js.html.ParagraphElement",
			HTMLParamElement: "js.html.ParamElement",
			HTMLPreElement: "js.html.PreElement",
			HTMLProgressElement: "js.html.ProgressElement",
			HTMLQuoteElement: "js.html.QuoteElement",
			HTMLScriptElement: "js.html.ScriptElement",
			HTMLSelectElement: "js.html.SelectElement",
			HTMLShadowElement: "js.html.ShadowElement",
			HTMLSourceElement: "js.html.SourceElement",
			HTMLSpanElement: "js.html.SpanElement",
			HTMLStyleElement: "js.html.StyleElement",
			HTMLTableCaptionElement: "js.html.TableCaptionElement",
			HTMLTableCellElement: "js.html.TableCellElement",
			HTMLTableColElement: "js.html.TableColElement",
			HTMLTableElement: "js.html.TableElement",
			HTMLTableRowElement: "js.html.TableRowElement",
			HTMLTableSectionElement: "js.html.TableSectionElement",
			HTMLTextAreaElement: "js.html.TextAreaElement",
			HTMLTitleElement: "js.html.TitleElement",
			HTMLTrackElement: "js.html.TrackElement",
			HTMLUListElement: "js.html.UListElement",
			HTMLUnknownElement: "js.html.UnknownElement",
			HTMLVideoElement: "js.html.VideoElement",
			HashChangeEvent: "js.html.HashChangeEvent",
			History: "js.html.History",
			IDBAny: "js.html.idb.Any",
			IDBCursor: "js.html.idb.Cursor",
			IDBCursorWithValue: "js.html.idb.CursorWithValue",
			IDBDatabase: "js.html.idb.Database",
			IDBDatabaseException: "js.html.idb.DatabaseException",
			IDBFactory: "js.html.idb.Factory",
			IDBIndex: "js.html.idb.Index",
			IDBKey: "js.html.idb.Key",
			IDBKeyRange: "js.html.idb.KeyRange",
			IDBObjectStore: "js.html.idb.ObjectStore",
			IDBOpenDBRequest: "js.html.idb.OpenDBRequest",
			IDBRequest: "js.html.idb.Request",
			IDBTransaction: "js.html.idb.Transaction",
			IDBVersionChangeEvent: "js.html.idb.UpgradeNeededEvent",
			IDBVersionChangeEvent: "js.html.idb.VersionChangeEvent",
			IDBVersionChangeRequest: "js.html.idb.VersionChangeRequest",
			Image: "js.html.Image",
			ImageData: "js.html.ImageData",
			Int16Array: "js.html.Int16Array",
			Int32Array: "js.html.Int32Array",
			Int8Array: "js.html.Int8Array",
			JSON: "haxe.Json",
			JavaScriptCallFrame: "js.html.JavaScriptCallFrame",
			KeyboardEvent: "js.html.KeyboardEvent",
			LocalMediaStream: "js.html.rtc.LocalMediaStream",
			Location: "js.html.Location",
			MediaController: "js.html.MediaController",
			MediaElementAudioSourceNode: "js.html.audio.MediaElementAudioSourceNode",
			MediaError: "js.html.MediaError",
			MediaKeyError: "js.html.MediaKeyError",
			MediaKeyEvent: "js.html.MediaKeyEvent",
			MediaList: "js.html.MediaList",
			MediaQueryList: "js.html.MediaQueryList",
			MediaSource: "js.html.MediaSource",
			MediaStream: "js.html.rtc.MediaStream",
			MediaStreamAudioSourceNode: "js.html.audio.MediaStreamAudioSourceNode",
			MediaStreamEvent: "js.html.rtc.MediaStreamEvent",
			MediaStreamList: "js.html.rtc.MediaStreamList",
			MediaStreamTrack: "js.html.rtc.MediaStreamTrack",
			MediaStreamTrackEvent: "js.html.rtc.MediaStreamTrackEvent",
			MediaStreamTrackList: "js.html.rtc.MediaStreamTrackList",
			MemoryInfo: "js.html.MemoryInfo",
			MessageChannel: "js.html.MessageChannel",
			MessageEvent: "js.html.MessageEvent",
			MessagePort: "js.html.MessagePort",
			Metadata: "js.html.fs.Metadata",
			MimeType: "js.html.DOMMimeType",
			MimeTypeArray: "js.html.DOMMimeTypeArray",
			MouseEvent: "js.html.MouseEvent",
			MutationEvent: "js.html.MutationEvent",
			MutationObserver: "js.html.MutationObserver",
			MutationRecord: "js.html.MutationRecord",
			NamedFlow: "js.html.NamedFlow",
			NamedNodeMap: "js.html.NamedNodeMap",
			Navigator: "js.html.Navigator",
			NavigatorUserMediaError: "js.html.rtc.NavigatorUserMediaError",
			Node: "js.html.Node",
			NodeFilter: "js.html.NodeFilter",
			NodeIterator: "js.html.NodeIterator",
			NodeList: "js.html.NodeList",
			Notation: "js.html.Notation",
			Notification: "js.html.Notification",
			NotificationCenter: "js.html.NotificationCenter",
			OESElementIndexUint: "js.html.webgl.OESElementIndexUint",
			OESStandardDerivatives: "js.html.webgl.OESStandardDerivatives",
			OESTextureFloat: "js.html.webgl.OESTextureFloat",
			OESVertexArrayObject: "js.html.webgl.OESVertexArrayObject",
			OfflineAudioCompletionEvent: "js.html.audio.OfflineAudioCompletionEvent",
			OscillatorNode: "js.html.audio.OscillatorNode",
			OverflowEvent: "js.html.OverflowEvent",
			PagePopupController: "js.html.PagePopupController",
			PageTransitionEvent: "js.html.PageTransitionEvent",
			PannerNode: "js.html.audio.PannerNode",
			Performance: "js.html.Performance",
			PerformanceNavigation: "js.html.PerformanceNavigation",
			PerformanceTiming: "js.html.PerformanceTiming",
			Plugin: "js.html.DOMPlugin",
			PluginArray: "js.html.DOMPluginArray",
			Point: "js.html.Point",
			PopStateEvent: "js.html.PopStateEvent",
			PositionError: "js.html.PositionError",
			ProcessingInstruction: "js.html.ProcessingInstruction",
			ProgressEvent: "js.html.ProgressEvent",
			RGBColor: "js.html.RGBColor",
			RTCDataChannel: "js.html.rtc.DataChannel",
			RTCDataChannelEvent: "js.html.rtc.DataChannelEvent",
			RTCIceCandidate: "js.html.rtc.IceCandidate",
			RTCIceCandidateEvent: "js.html.rtc.IceCandidateEvent",
			RTCPeerConnection: "js.html.rtc.PeerConnection",
			RTCSessionDescription: "js.html.rtc.SessionDescription",
			RTCStatsElement: "js.html.rtc.StatsElement",
			RTCStatsReport: "js.html.rtc.StatsReport",
			RTCStatsResponse: "js.html.rtc.StatsResponse",
			RadioNodeList: "js.html.RadioNodeList",
			Range: "js.html.Range",
			RangeException: "js.html.RangeException",
			Rect: "js.html.Rect",
			SQLError: "js.html.sql.Error",
			SQLException: "js.html.sql.Exception",
			SQLResultSet: "js.html.sql.ResultSet",
			SQLResultSetRowList: "js.html.sql.ResultSetRowList",
			SQLTransaction: "js.html.sql.Transaction",
			SQLTransactionSync: "js.html.sql.TransactionSync",
			SVGAElement: "js.html.svg.AElement",
			SVGAltGlyphDefElement: "js.html.svg.AltGlyphDefElement",
			SVGAltGlyphElement: "js.html.svg.AltGlyphElement",
			SVGAltGlyphItemElement: "js.html.svg.AltGlyphItemElement",
			SVGAngle: "js.html.svg.Angle",
			SVGAnimateColorElement: "js.html.svg.AnimateColorElement",
			SVGAnimateElement: "js.html.svg.AnimateElement",
			SVGAnimateMotionElement: "js.html.svg.AnimateMotionElement",
			SVGAnimateTransformElement: "js.html.svg.AnimateTransformElement",
			SVGAnimatedAngle: "js.html.svg.AnimatedAngle",
			SVGAnimatedBoolean: "js.html.svg.AnimatedBoolean",
			SVGAnimatedEnumeration: "js.html.svg.AnimatedEnumeration",
			SVGAnimatedInteger: "js.html.svg.AnimatedInteger",
			SVGAnimatedLength: "js.html.svg.AnimatedLength",
			SVGAnimatedLengthList: "js.html.svg.AnimatedLengthList",
			SVGAnimatedNumber: "js.html.svg.AnimatedNumber",
			SVGAnimatedNumberList: "js.html.svg.AnimatedNumberList",
			SVGAnimatedPreserveAspectRatio: "js.html.svg.AnimatedPreserveAspectRatio",
			SVGAnimatedRect: "js.html.svg.AnimatedRect",
			SVGAnimatedString: "js.html.svg.AnimatedString",
			SVGAnimatedTransformList: "js.html.svg.AnimatedTransformList",
			SVGAnimationElement: "js.html.svg.AnimationElement",
			SVGCircleElement: "js.html.svg.CircleElement",
			SVGClipPathElement: "js.html.svg.ClipPathElement",
			SVGColor: "js.html.svg.Color",
			SVGComponentTransferFunctionElement: "js.html.svg.ComponentTransferFunctionElement",
			SVGCursorElement: "js.html.svg.CursorElement",
			SVGDefsElement: "js.html.svg.DefsElement",
			SVGDescElement: "js.html.svg.DescElement",
			SVGDocument: "js.html.svg.Document",
			SVGElement: "js.html.svg.Element",
			SVGElementInstance: "js.html.svg.ElementInstance",
			SVGElementInstanceList: "js.html.svg.ElementInstanceList",
			SVGEllipseElement: "js.html.svg.EllipseElement",
			SVGException: "js.html.svg.Exception",
			SVGExternalResourcesRequired: "js.html.svg.ExternalResourcesRequired",
			SVGFEBlendElement: "js.html.svg.FEBlendElement",
			SVGFEColorMatrixElement: "js.html.svg.FEColorMatrixElement",
			SVGFEComponentTransferElement: "js.html.svg.FEComponentTransferElement",
			SVGFECompositeElement: "js.html.svg.FECompositeElement",
			SVGFEConvolveMatrixElement: "js.html.svg.FEConvolveMatrixElement",
			SVGFEDiffuseLightingElement: "js.html.svg.FEDiffuseLightingElement",
			SVGFEDisplacementMapElement: "js.html.svg.FEDisplacementMapElement",
			SVGFEDistantLightElement: "js.html.svg.FEDistantLightElement",
			SVGFEDropShadowElement: "js.html.svg.FEDropShadowElement",
			SVGFEFloodElement: "js.html.svg.FEFloodElement",
			SVGFEFuncAElement: "js.html.svg.FEFuncAElement",
			SVGFEFuncBElement: "js.html.svg.FEFuncBElement",
			SVGFEFuncGElement: "js.html.svg.FEFuncGElement",
			SVGFEFuncRElement: "js.html.svg.FEFuncRElement",
			SVGFEGaussianBlurElement: "js.html.svg.FEGaussianBlurElement",
			SVGFEImageElement: "js.html.svg.FEImageElement",
			SVGFEMergeElement: "js.html.svg.FEMergeElement",
			SVGFEMergeNodeElement: "js.html.svg.FEMergeNodeElement",
			SVGFEMorphologyElement: "js.html.svg.FEMorphologyElement",
			SVGFEOffsetElement: "js.html.svg.FEOffsetElement",
			SVGFEPointLightElement: "js.html.svg.FEPointLightElement",
			SVGFESpecularLightingElement: "js.html.svg.FESpecularLightingElement",
			SVGFESpotLightElement: "js.html.svg.FESpotLightElement",
			SVGFETileElement: "js.html.svg.FETileElement",
			SVGFETurbulenceElement: "js.html.svg.FETurbulenceElement",
			SVGFilterElement: "js.html.svg.FilterElement",
			SVGFilterPrimitiveStandardAttributes: "js.html.svg.FilterPrimitiveStandardAttributes",
			SVGFitToViewBox: "js.html.svg.FitToViewBox",
			SVGFontElement: "js.html.svg.FontElement",
			SVGFontFaceElement: "js.html.svg.FontFaceElement",
			SVGFontFaceFormatElement: "js.html.svg.FontFaceFormatElement",
			SVGFontFaceNameElement: "js.html.svg.FontFaceNameElement",
			SVGFontFaceSrcElement: "js.html.svg.FontFaceSrcElement",
			SVGFontFaceUriElement: "js.html.svg.FontFaceUriElement",
			SVGForeignObjectElement: "js.html.svg.ForeignObjectElement",
			SVGGElement: "js.html.svg.GElement",
			SVGGlyphElement: "js.html.svg.GlyphElement",
			SVGGlyphRefElement: "js.html.svg.GlyphRefElement",
			SVGGradientElement: "js.html.svg.GradientElement",
			SVGHKernElement: "js.html.svg.HKernElement",
			SVGImageElement: "js.html.svg.ImageElement",
			SVGLangSpace: "js.html.svg.LangSpace",
			SVGLength: "js.html.svg.Length",
			SVGLengthList: "js.html.svg.LengthList",
			SVGLineElement: "js.html.svg.LineElement",
			SVGLinearGradientElement: "js.html.svg.LinearGradientElement",
			SVGLocatable: "js.html.svg.Locatable",
			SVGMPathElement: "js.html.svg.MPathElement",
			SVGMarkerElement: "js.html.svg.MarkerElement",
			SVGMaskElement: "js.html.svg.MaskElement",
			SVGMatrix: "js.html.svg.Matrix",
			SVGMetadataElement: "js.html.svg.MetadataElement",
			SVGMissingGlyphElement: "js.html.svg.MissingGlyphElement",
			SVGNumber: "js.html.svg.Number",
			SVGNumberList: "js.html.svg.NumberList",
			SVGPaint: "js.html.svg.Paint",
			SVGPathElement: "js.html.svg.PathElement",
			SVGPathSeg: "js.html.svg.PathSeg",
			SVGPathSegArcAbs: "js.html.svg.PathSegArcAbs",
			SVGPathSegArcRel: "js.html.svg.PathSegArcRel",
			SVGPathSegClosePath: "js.html.svg.PathSegClosePath",
			SVGPathSegCurvetoCubicAbs: "js.html.svg.PathSegCurvetoCubicAbs",
			SVGPathSegCurvetoCubicRel: "js.html.svg.PathSegCurvetoCubicRel",
			SVGPathSegCurvetoCubicSmoothAbs: "js.html.svg.PathSegCurvetoCubicSmoothAbs",
			SVGPathSegCurvetoCubicSmoothRel: "js.html.svg.PathSegCurvetoCubicSmoothRel",
			SVGPathSegCurvetoQuadraticAbs: "js.html.svg.PathSegCurvetoQuadraticAbs",
			SVGPathSegCurvetoQuadraticRel: "js.html.svg.PathSegCurvetoQuadraticRel",
			SVGPathSegCurvetoQuadraticSmoothAbs: "js.html.svg.PathSegCurvetoQuadraticSmoothAbs",
			SVGPathSegCurvetoQuadraticSmoothRel: "js.html.svg.PathSegCurvetoQuadraticSmoothRel",
			SVGPathSegLinetoAbs: "js.html.svg.PathSegLinetoAbs",
			SVGPathSegLinetoHorizontalAbs: "js.html.svg.PathSegLinetoHorizontalAbs",
			SVGPathSegLinetoHorizontalRel: "js.html.svg.PathSegLinetoHorizontalRel",
			SVGPathSegLinetoRel: "js.html.svg.PathSegLinetoRel",
			SVGPathSegLinetoVerticalAbs: "js.html.svg.PathSegLinetoVerticalAbs",
			SVGPathSegLinetoVerticalRel: "js.html.svg.PathSegLinetoVerticalRel",
			SVGPathSegList: "js.html.svg.PathSegList",
			SVGPathSegMovetoAbs: "js.html.svg.PathSegMovetoAbs",
			SVGPathSegMovetoRel: "js.html.svg.PathSegMovetoRel",
			SVGPatternElement: "js.html.svg.PatternElement",
			SVGPoint: "js.html.svg.Point",
			SVGPointList: "js.html.svg.PointList",
			SVGPolygonElement: "js.html.svg.PolygonElement",
			SVGPolylineElement: "js.html.svg.PolylineElement",
			SVGPreserveAspectRatio: "js.html.svg.PreserveAspectRatio",
			SVGRadialGradientElement: "js.html.svg.RadialGradientElement",
			SVGRect: "js.html.svg.Rect",
			SVGRectElement: "js.html.svg.RectElement",
			SVGRenderingIntent: "js.html.svg.RenderingIntent",
			SVGSVGElement: "js.html.svg.SVGElement",
			SVGScriptElement: "js.html.svg.ScriptElement",
			SVGSetElement: "js.html.svg.SetElement",
			SVGStopElement: "js.html.svg.StopElement",
			SVGStringList: "js.html.svg.StringList",
			SVGStylable: "js.html.svg.Stylable",
			SVGStyleElement: "js.html.svg.StyleElement",
			SVGSwitchElement: "js.html.svg.SwitchElement",
			SVGSymbolElement: "js.html.svg.SymbolElement",
			SVGTRefElement: "js.html.svg.TRefElement",
			SVGTSpanElement: "js.html.svg.TSpanElement",
			SVGTests: "js.html.svg.Tests",
			SVGTextContentElement: "js.html.svg.TextContentElement",
			SVGTextElement: "js.html.svg.TextElement",
			SVGTextPathElement: "js.html.svg.TextPathElement",
			SVGTextPositioningElement: "js.html.svg.TextPositioningElement",
			SVGTitleElement: "js.html.svg.TitleElement",
			SVGTransform: "js.html.svg.Transform",
			SVGTransformList: "js.html.svg.TransformList",
			SVGTransformable: "js.html.svg.Transformable",
			SVGURIReference: "js.html.svg.URIReference",
			SVGUnitTypes: "js.html.svg.UnitTypes",
			SVGUseElement: "js.html.svg.UseElement",
			SVGVKernElement: "js.html.svg.VKernElement",
			SVGViewElement: "js.html.svg.ViewElement",
			SVGViewSpec: "js.html.svg.ViewSpec",
			SVGZoomAndPan: "js.html.svg.ZoomAndPan",
			SVGZoomEvent: "js.html.svg.ZoomEvent",
			Screen: "js.html.Screen",
			ScriptProcessorNode: "js.html.audio.ScriptProcessorNode",
			ScriptProfile: "js.html.ScriptProfile",
			ScriptProfileNode: "js.html.ScriptProfileNode",
			Selection: "js.html.DOMSelection",
			ShadowRoot: "js.html.ShadowRoot",
			SharedWorker: "js.html.SharedWorker",
			SharedWorkerContext: "js.html.SharedWorkerContext",
			SourceBuffer: "js.html.SourceBuffer",
			SourceBufferList: "js.html.SourceBufferList",
			SpeechGrammar: "js.html.SpeechGrammar",
			SpeechGrammarList: "js.html.SpeechGrammarList",
			SpeechInputEvent: "js.html.SpeechInputEvent",
			SpeechInputResult: "js.html.SpeechInputResult",
			SpeechInputResultList: "js.html.SpeechInputResultList",
			SpeechRecognition: "js.html.SpeechRecognition",
			SpeechRecognitionAlternative: "js.html.SpeechRecognitionAlternative",
			SpeechRecognitionError: "js.html.SpeechRecognitionError",
			SpeechRecognitionEvent: "js.html.SpeechRecognitionEvent",
			SpeechRecognitionResult: "js.html.SpeechRecognitionResult",
			SpeechRecognitionResultList: "js.html.SpeechRecognitionResultList",
			Storage: "js.html.Storage",
			StorageEvent: "js.html.StorageEvent",
			StorageInfo: "js.html.StorageInfo",
			StyleMedia: "js.html.StyleMedia",
			StyleSheet: "js.html.StyleSheet",
			StyleSheetList: "js.html.StyleSheetList",
			Text: "js.html.Text",
			TextEvent: "js.html.TextEvent",
			TextMetrics: "js.html.TextMetrics",
			TextTrack: "js.html.TextTrack",
			TextTrackCue: "js.html.TextTrackCue",
			TextTrackCueList: "js.html.TextTrackCueList",
			TextTrackList: "js.html.TextTrackList",
			TimeRanges: "js.html.TimeRanges",
			Touch: "js.html.Touch",
			TouchEvent: "js.html.TouchEvent",
			TouchList: "js.html.TouchList",
			TrackEvent: "js.html.TrackEvent",
			TransitionEvent: "js.html.TransitionEvent",
			TreeWalker: "js.html.TreeWalker",
			UIEvent: "js.html.UIEvent",
			URL: "js.html.DOMURL",
			Uint16Array: "js.html.Uint16Array",
			Uint32Array: "js.html.Uint32Array",
			Uint8Array: "js.html.Uint8Array",
			Uint8ClampedArray: "js.html.Uint8ClampedArray",
			ValidityState: "js.html.ValidityState",
			WaveShaperNode: "js.html.audio.WaveShaperNode",
			WaveTable: "js.html.audio.WaveTable",
			WebGLActiveInfo: "js.html.webgl.ActiveInfo",
			WebGLBuffer: "js.html.webgl.Buffer",
			WebGLCompressedTextureS3TC: "js.html.webgl.CompressedTextureS3TC",
			WebGLContextEvent: "js.html.webgl.ContextEvent",
			WebGLDebugRendererInfo: "js.html.webgl.DebugRendererInfo",
			WebGLDebugShaders: "js.html.webgl.DebugShaders",
			WebGLDepthTexture: "js.html.webgl.DepthTexture",
			WebGLFramebuffer: "js.html.webgl.Framebuffer",
			WebGLLoseContext: "js.html.webgl.LoseContext",
			WebGLProgram: "js.html.webgl.Program",
			WebGLRenderbuffer: "js.html.webgl.Renderbuffer",
			WebGLRenderingContext: "js.html.webgl.RenderingContext",
			WebGLShader: "js.html.webgl.Shader",
			WebGLShaderPrecisionFormat: "js.html.webgl.ShaderPrecisionFormat",
			WebGLTexture: "js.html.webgl.Texture",
			WebGLUniformLocation: "js.html.webgl.UniformLocation",
			WebGLVertexArrayObjectOES: "js.html.webgl.VertexArrayObjectOES",
			WebSocket: "js.html.WebSocket",
			WheelEvent: "js.html.WheelEvent",
			Window: "js.html.DOMWindow",
			Worker: "js.html.Worker",
			WorkerContext: "js.html.WorkerContext",
			WorkerLocation: "js.html.WorkerLocation",
			WorkerNavigator: "js.html.WorkerNavigator",
			XMLHttpRequest: "js.html.XMLHttpRequest",
			XMLHttpRequestException: "js.html.XMLHttpRequestException",
			XMLHttpRequestProgressEvent: "js.html.XMLHttpRequestProgressEvent",
			XMLHttpRequestUpload: "js.html.XMLHttpRequestUpload",
			XMLSerializer: "js.html.XMLSerializer",
			XPathEvaluator: "js.html.XPathEvaluator",
			XPathException: "js.html.XPathException",
			XPathExpression: "js.html.XPathExpression",
			XPathNSResolver: "js.html.XPathNSResolver",
			XPathResult: "js.html.XPathResult",
			XSLTProcessor: "js.html.XSLTProcessor",
	].asImmutable()
}
