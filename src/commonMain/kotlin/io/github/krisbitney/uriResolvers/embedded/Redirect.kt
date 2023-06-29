package io.github.krisbitney.uriResolvers.embedded

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.WrapPackage
import io.github.krisbitney.core.types.Wrapper

/** Associates a URI with a URI to redirect to. */
typealias UriRedirect = Pair<Uri, Uri>

/** Associates a URI with an embedded wrap package. */
typealias PackageRedirect = Pair<Uri, WrapPackage>

/** Associates a URI with an embedded wrapper. */
typealias WrapperRedirect = Pair<Uri, Wrapper>
